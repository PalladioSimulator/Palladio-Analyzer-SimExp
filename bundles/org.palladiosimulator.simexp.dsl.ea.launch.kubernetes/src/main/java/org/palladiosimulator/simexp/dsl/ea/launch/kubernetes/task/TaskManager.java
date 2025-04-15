package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.concurrent.SettableFutureTask;

public class TaskManager implements ITaskManager, ITaskConsumer {
    private static final Logger LOGGER = Logger.getLogger(TaskManager.class);

    private Map<String, SettableFutureTask<Optional<Double>>> outstandingTasks;

    private int receivedCount = 0;
    private int taskCount = 0;

    public TaskManager() {
        this.outstandingTasks = new HashMap<>();
    }

    @Override
    public synchronized void newTask(String taskId, SettableFutureTask<Optional<Double>> task) {
        taskCount++;
        outstandingTasks.put(taskId, task);
    }

    @Override
    public synchronized void taskCompleted(String taskId, JobResult result) {
        int count = ++receivedCount;

        SettableFutureTask<Optional<Double>> future = outstandingTasks.remove(taskId);
        if (future == null) {
            LOGGER.error(String.format("received unknown answer: %s", taskId));
            return;
        }

        String description = getRewardDescription(result);
        LOGGER.info(String.format("received answer %d/%d [%s] reward: %s", count, taskCount, result.id, description));

        if (StringUtils.isNotEmpty(result.error)) {
            future.setResult(Optional.empty());
        } else {
            future.setResult(Optional.of(result.reward));
        }
    }

    private String getRewardDescription(JobResult result) {
        if (StringUtils.isNotEmpty(result.error)) {
            String.format("n/a (%s)", result.error);
        }
        return String.format("%s", result.reward);
    }

}
