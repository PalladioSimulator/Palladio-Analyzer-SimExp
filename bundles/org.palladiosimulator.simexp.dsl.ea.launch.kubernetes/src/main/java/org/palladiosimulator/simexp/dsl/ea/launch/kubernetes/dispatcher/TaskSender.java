package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.dispatcher;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.JobTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rabbitmq.client.Channel;

public class TaskSender implements ITaskSender {
    private static final Logger LOGGER = Logger.getLogger(TaskSender.class);

    private final Channel channel;
    private final String outQueueName;

    public TaskSender(Channel channel, String outQueueName) {
        this.channel = channel;
        this.outQueueName = outQueueName;
    }

    @Override
    public void sendTask(JobTask task, String description) throws IOException {
        Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(byte[].class, new ByteArrayToBase64TypeAdapter())
            .create();

        String message = gson.toJson(task);
        channel.basicPublish("", outQueueName, null, message.getBytes(StandardCharsets.UTF_8));
        LOGGER.info(String.format("Sent task: %s [%s]", task.id, description));
    }

}
