package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
    private final Set<String> startedTasks;

    private int receivedCount = 0;
    private int taskCount = 0;

    public TaskManager(IResultLogger resultLogger) {
        this.resultLogger = resultLogger;
        this.outstandingTasks = new HashMap<>();
        this.startedTasks = new HashSet<>();
    }

    @Override
    public synchronized void newTask(String taskId, SettableFutureTask<Optional<Double>> task,
            List<OptimizableValue<?>> optimizableValues) {
        taskCount++;
        outstandingTasks.put(taskId, new TaskInfo(task, optimizableValues));
    }

    @Override
    public void taskStarted(String taskId, JobResult result) {
        final int received;
        final int started;
        final int created;
        synchronized (this) {
            startedTasks.add(taskId);
            received = receivedCount;
            started = startedTasks.size();
            created = taskCount;
        }
        String tasksStatus = getTasksStatus("started", received, started, created);
        LOGGER.info(String.format("%s [%s] by %s (redelivered: %s)", tasksStatus, result.id, result.executor_id,
                result.redelivered));
    }

    @Override
    public void taskCompleted(String taskId, JobResult result) {
        final int received;
        final int started;
        final int created;
        final TaskInfo taskInfo;
        synchronized (this) {
            startedTasks.remove(taskId);
            received = ++receivedCount;
            created = taskCount;
            started = startedTasks.size();
            taskInfo = outstandingTasks.remove(taskId);
        }
        if (taskInfo == null) {
            LOGGER.error(String.format("received unknown answer: %s", taskId));
            return;
        }

        String tasksStatus = getTasksStatus("completed", received, started, created);
        String description = getRewardDescription(result);
        LOGGER.info(String.format("%s [%s] by %s reward: %s", tasksStatus, result.id, result.executor_id, description));

        SettableFutureTask<Optional<Double>> future = taskInfo.future;
        resultLogger.log(taskInfo.optimizableValues, result);
        if (result.reward == null) {
            future.setResult(Optional.empty());
        } else {
            future.setResult(Optional.of(result.reward));
        }
    }

    private String getTasksStatus(String status, int received, int started, int created) {
        String tasksStatus = String.format("task %s %d/%d/%d", status, started, received, created);
        return tasksStatus;
    }

    private String getRewardDescription(JobResult result) {
        if (result.reward == null) {
            return String.format("<null> (%s)", result.error);
        }
        return String.format("%s", result.reward);
    }

}
