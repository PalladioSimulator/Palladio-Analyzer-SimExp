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

    private int counter = 0;

    public TaskManager() {
        this.outstandingTasks = new HashMap<>();
    }

    @Override
    public synchronized void newTask(String taskId, SettableFutureTask<Optional<Double>> task) {
        outstandingTasks.put(taskId, task);
    }

    @Override
    public synchronized void taskCompleted(String taskId, JobResult result) {
        int count = ++counter;

        LOGGER.info(String.format("received answer %d [%s] reward: %s (%s)", count, result.id, result.reward,
                result.error));

        SettableFutureTask<Optional<Double>> future = outstandingTasks.get(taskId);
        if (future == null) {
            LOGGER.error(String.format("received unknown task: %s", taskId));
            return;
        }

        if (StringUtils.isNotEmpty(result.error)) {
            // TODO: use on error value
            future.setResult(Optional.of(0.0));
        } else {
            future.setResult(Optional.of(result.reward));
        }
    }

}
