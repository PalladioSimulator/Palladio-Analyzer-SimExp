package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.JobResult.Status;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class TaskReceiver extends DefaultConsumer implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(TaskReceiver.class);

    private final Gson gson;
    private final List<ITaskConsumer> taskConsumers;
    private final ClassLoader classloader;

    public TaskReceiver(Channel channel, String queueName, ClassLoader classloader) throws IOException {
        super(channel);
        this.gson = new Gson();
        this.taskConsumers = new ArrayList<>();
        this.classloader = classloader;
        boolean autoAck = false;
        channel.basicConsume(queueName, autoAck, "answerConsumer", this);
    }

    @Override
    public void close() throws IOException {
        getChannel().basicCancel("answerConsumer");
    }

    public synchronized void registerTaskConsumer(ITaskConsumer taskConsumer) {
        taskConsumers.add(taskConsumer);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
            throws IOException {
        ClassLoader oldContextClassLoader = Thread.currentThread()
            .getContextClassLoader();
        Thread.currentThread()
            .setContextClassLoader(classloader);
        try {
            long deliveryTag = envelope.getDeliveryTag();
            String message = new String(body, StandardCharsets.UTF_8);
            LOGGER.debug(String.format("received message tag %d: %s", deliveryTag, message));
            JobResult answer = gson.fromJson(message, JobResult.class);
            notifyConsumers(answer.id, answer);
            getChannel().basicAck(deliveryTag, false);
        } finally {
            Thread.currentThread()
                .setContextClassLoader(oldContextClassLoader);
        }
    }

    private void notifyConsumers(String answerId, JobResult result) {
        final List<ITaskConsumer> consumers;
        synchronized (this) {
            consumers = new ArrayList<>(taskConsumers);
        }
        for (ITaskConsumer consumer : consumers) {
            if (result.status == Status.START) {
                consumer.taskStarted(answerId, result);
            } else if (result.status == Status.COMPLETE) {
                consumer.taskCompleted(answerId, result);
            }
        }
    }
}