package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.util.OptimizableValueToString;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.concurrent.SettableFutureTask;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class TaskManager implements ITaskManager, ITaskConsumer {
    private static final Logger LOGGER = Logger.getLogger(TaskManager.class);
    private static final int MAX_ABORTIONS = 3;

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
    private final Counter<String> abortedTasks;

    private int receivedCount = 0;
    private int taskCount = 0;

    public TaskManager(IResultLogger resultLogger) {
        this.resultLogger = resultLogger;
        this.outstandingTasks = new HashMap<>();
        this.startedTasks = new HashSet<>();
        this.abortedTasks = new Counter<>();
    }

    @Override
    public void newTask(String taskId, SettableFutureTask<Optional<Double>> task,
            List<OptimizableValue<?>> optimizableValues) {
        final int completed;
        final int started;
        final int aborted;
        final int created;
        synchronized (this) {
            taskCount++;
            outstandingTasks.put(taskId, new TaskInfo(task, optimizableValues));
            started = startedTasks.size();
            completed = receivedCount;
            aborted = abortedTasks.size();
            created = taskCount;
        }
        OptimizableValueToString optimizableValueToString = new OptimizableValueToString();
        String description = optimizableValueToString.asString(optimizableValues);
        String tasksStatus = getTasksStatus("created", completed, started, aborted, created);
        LOGGER.info(String.format("%s [%s]: %s", tasksStatus, taskId, description));
    }

    @Override
    public void taskStarted(String taskId, JobResult result) {
        final int completed;
        final int started;
        final int aborted;
        final int created;
        synchronized (this) {
            startedTasks.add(taskId);
            started = startedTasks.size();
            completed = receivedCount;
            aborted = abortedTasks.size();
            created = taskCount;
        }
        String tasksStatus = getTasksStatus("started", completed, started, aborted, created);
        LOGGER.info(String.format("%s [%s] by %s (redelivered: %s)", tasksStatus, result.id, result.executor_id,
                result.redelivered));
    }

    @Override
    public void taskCompleted(String taskId, JobResult result) {
        final int completed;
        final int started;
        final int aborted;
        final int created;
        final TaskInfo taskInfo;
        synchronized (this) {
            startedTasks.remove(taskId);
            started = startedTasks.size();
            completed = ++receivedCount;
            abortedTasks.remove(taskId);
            aborted = abortedTasks.size();
            created = taskCount;
            taskInfo = outstandingTasks.remove(taskId);
        }
        if (taskInfo == null) {
            LOGGER.error(String.format("received unknown answer: %s", taskId));
            return;
        }

        String tasksStatus = getTasksStatus("completed", completed, started, aborted, created);
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

    @Override
    public void taskAborted(String taskId, JobResult result) {
        final int completed;
        final int started;
        final int aborted;
        final int created;
        final boolean consideredFailed;
        final TaskInfo taskInfo;
        synchronized (this) {
            started = startedTasks.size();
            completed = receivedCount;
            abortedTasks.add(taskId);
            aborted = abortedTasks.size();
            created = taskCount;
            consideredFailed = abortedTasks.get(taskId) >= MAX_ABORTIONS;
            if (consideredFailed) {
                taskInfo = outstandingTasks.remove(taskId);
            } else {
                taskInfo = outstandingTasks.get(taskId);
            }
        }
        String tasksStatus = getTasksStatus("aborted", completed, started, aborted, created);
        LOGGER.info(String.format("%s [%s] by %s (%s)", tasksStatus, taskId, result.executor_id, result.error));
        if (consideredFailed && (taskInfo != null)) {
            LOGGER.info(String.format("too many abortions for %s -> permanent failure", taskId));
            SettableFutureTask<Optional<Double>> future = taskInfo.future;
            resultLogger.log(taskInfo.optimizableValues, result);
            future.setResult(Optional.empty());
        }
    }

    private String getTasksStatus(String status, int completed, int started, int aborted, int created) {
        String tasksStatus = String.format("task %s %d/%d/(%d)/%d", status, started, completed, aborted, created);
        return tasksStatus;
    }

    private String getRewardDescription(JobResult result) {
        if (result.reward == null) {
            return String.format("<null> (%s)", result.error);
        }
        return String.format("%s", result.reward);
    }
}
