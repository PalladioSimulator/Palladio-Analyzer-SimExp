package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class TaskReceiver extends DefaultConsumer {
    private static final Logger LOGGER = Logger.getLogger(TaskReceiver.class);

    private final Map<String, JobResult> answers;
    private final List<ITaskConsumer> taskConsumers;
    private final ClassLoader classloader;

    public TaskReceiver(Channel channel, ClassLoader classloader) {
        super(channel);
        this.answers = new HashMap<>();
        this.taskConsumers = new ArrayList<>();
        this.classloader = classloader;
    }

    public synchronized void registerTaskConsumer(ITaskConsumer taskConsumer) {
        taskConsumers.add(taskConsumer);
    }

    public List<JobResult> getAnswers() {
        return new ArrayList<>(answers.values());
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
            Gson gson = new Gson();
            JobResult answer = gson.fromJson(message, JobResult.class);
            if (answers.containsKey(answer.id)) {
                throw new RuntimeException("already received: %s" + answer.id);
            }
            answers.put(answer.id, answer);
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
            consumer.taskCompleted(answerId, result);
        }
    }
}