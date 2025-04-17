package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.concurrent.SettableFutureTask;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class TaskManager implements ITaskManager, ITaskConsumer {
    private static final Logger LOGGER = Logger.getLogger(TaskManager.class);

    private static class TaskInfo {
        public final SettableFutureTask<Optional<Double>> future;
        public final List<OptimizableValue<?>> optimizableValues;

        public TaskInfo(SettableFutureTask<Optional<Double>> future, List<OptimizableValue<?>> optimizableValues) {
            this.future = future;
            this.optimizableValues = new ArrayList<>(optimizableValues);
        }
    }

    private final IResultLogger resultLogger;
    private final Map<String, TaskInfo> outstandingTasks;

    private int receivedCount = 0;
    private int taskCount = 0;

    public TaskManager(IResultLogger resultLogger) {
        this.resultLogger = resultLogger;
        this.outstandingTasks = new HashMap<>();
    }

    @Override
    public synchronized void newTask(String taskId, SettableFutureTask<Optional<Double>> task,
            List<OptimizableValue<?>> optimizableValues) {
        taskCount++;
        outstandingTasks.put(taskId, new TaskInfo(task, optimizableValues));
    }

    @Override
    public void taskCompleted(String taskId, JobResult result) {
        final int count;
        final TaskInfo taskInfo;
        synchronized (this) {
            count = ++receivedCount;
            taskInfo = outstandingTasks.remove(taskId);
        }
        if (taskInfo == null) {
            LOGGER.error(String.format("received unknown answer: %s", taskId));
            return;
        }

        String description = getRewardDescription(result);
        LOGGER.info(String.format("received answer %d/%d [%s] reward: %s", count, taskCount, result.id, description));

        SettableFutureTask<Optional<Double>> future = taskInfo.future;
        resultLogger.log(taskInfo.optimizableValues, result);
        if (StringUtils.isNotEmpty(result.error)) {
            future.setResult(Optional.empty());
        } else {
            future.setResult(Optional.of(result.reward));
        }
    }

    private String getRewardDescription(JobResult result) {
        if (StringUtils.isNotEmpty(result.error)) {
            return String.format("<null> (%s)", result.error);
        }
        return String.format("%s", result.reward);
    }

}
