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
    private final Set<String> abortedTasks;
    private final Set<String> failedTasks;

    private int receivedCount = 0;
    private int taskCount = 0;

    public TaskManager(IResultLogger resultLogger) {
        this.resultLogger = resultLogger;
        this.outstandingTasks = new HashMap<>();
        this.startedTasks = new HashSet<>();
        this.abortedTasks = new HashSet<>();
        this.failedTasks = new HashSet<>();
    }

    static class TaskState {
        public final String state;
        public final int completed;
        public final int started;
        public final int aborted;
        public final int failed;
        public final int created;
        public final TaskInfo taskInfo;

        public TaskState(String state, int completed, int started, int aborted, int failed, int created,
                TaskInfo taskInfo) {
            this.state = state;
            this.completed = completed;
            this.started = started;
            this.aborted = aborted;
            this.failed = failed;
            this.created = created;
            this.taskInfo = taskInfo;
        }
    }

    @Override
    public void newTask(String taskId, SettableFutureTask<Optional<Double>> task,
            List<OptimizableValue<?>> optimizableValues) {
        TaskState taskState = onTaskCreated(taskId, task, optimizableValues);
        OptimizableValueToString optimizableValueToString = new OptimizableValueToString();
        String description = optimizableValueToString.asString(optimizableValues);
        String tasksDescription = getTasksDescription(taskState);
        LOGGER.info(String.format("%s [%s]: %s", tasksDescription, taskId, description));
    }

    TaskState onTaskCreated(String taskId, SettableFutureTask<Optional<Double>> task,
            List<OptimizableValue<?>> optimizableValues) {
        final int completed;
        final int started;
        final int aborted;
        final int failed;
        final int created;
        synchronized (this) {
            taskCount++;
            outstandingTasks.put(taskId, new TaskInfo(task, optimizableValues));
            started = startedTasks.size();
            completed = receivedCount;
            aborted = abortedTasks.size();
            failed = failedTasks.size();
            created = taskCount;
        }
        return new TaskState("created", completed, started, aborted, failed, created, null);
    }

    @Override
    public void taskStarted(String taskId, JobResult result) {
        TaskState taskState = onTaskStarted(taskId, result);
        String tasksDescription = getTasksDescription(taskState);
        LOGGER.info(String.format("%s [%s] by %s (redelivered: %s %d)", tasksDescription, result.id, result.executor_id,
                result.redelivered, result.delivery_count));
    }

    TaskState onTaskStarted(String taskId, JobResult result) {
        final int completed;
        final int started;
        final int aborted;
        final int failed;
        final int created;
        synchronized (this) {
            startedTasks.add(taskId);
            started = startedTasks.size();
            completed = receivedCount;
            aborted = abortedTasks.size();
            failed = failedTasks.size();
            created = taskCount;
        }
        return new TaskState("started", completed, started, aborted, failed, created, null);
    }

    @Override
    public void taskCompleted(String taskId, JobResult result) {
        TaskState taskState = onTaskCompleted(taskId, result);
        if (taskState.taskInfo == null) {
            LOGGER.error(String.format("received unknown answer: %s", taskId));
            return;
        }

        String tasksDescription = getTasksDescription(taskState);
        String description = getRewardDescription(result);
        LOGGER.info(String.format("%s [%s] by %s reward: %s", tasksDescription, result.id, result.executor_id,
                description));

        SettableFutureTask<Optional<Double>> future = taskState.taskInfo.future;
        resultLogger.log(taskState.taskInfo.optimizableValues, result);
        if (result.reward == null) {
            future.setResult(Optional.empty());
        } else {
            future.setResult(Optional.of(result.reward));
        }
    }

    TaskState onTaskCompleted(String taskId, JobResult result) {
        final int completed;
        final int started;
        final int aborted;
        final int failed;
        final int created;
        final TaskInfo taskInfo;
        final String statusString;
        synchronized (this) {
            startedTasks.remove(taskId);
            started = startedTasks.size();
            if (result.reward != null) {
                statusString = "completed";
                completed = ++receivedCount;
            } else {
                statusString = "failed";
                completed = receivedCount;
                failedTasks.add(taskId);
            }
            abortedTasks.remove(taskId);
            aborted = abortedTasks.size();
            failed = failedTasks.size();
            created = taskCount;
            taskInfo = outstandingTasks.remove(taskId);
        }
        return new TaskState(statusString, completed, started, aborted, failed, created, taskInfo);
    }

    @Override
    public void taskAborted(String taskId, JobResult result) {
        TaskState taskState = onTaskAborted(taskId, result);
        if (taskState.taskInfo != null) {
            String tasksDescription = getTasksDescription(taskState);
            LOGGER
                .warn(String.format("%s [%s] by %s (%s)", tasksDescription, taskId, result.executor_id, result.error));
            SettableFutureTask<Optional<Double>> future = taskState.taskInfo.future;
            resultLogger.log(taskState.taskInfo.optimizableValues, result);
            future.setResult(Optional.empty());
        } else {
            LOGGER.warn(String.format("aborted task [%s] already completed/aborted", taskId));
        }
    }

    TaskState onTaskAborted(String taskId, JobResult result) {
        final int completed;
        final int started;
        final int aborted;
        final int failed;
        final int created;
        final TaskInfo taskInfo;
        synchronized (this) {
            startedTasks.remove(taskId);
            started = startedTasks.size();
            completed = receivedCount;
            abortedTasks.add(taskId);
            aborted = abortedTasks.size();
            failed = failedTasks.size();
            created = taskCount;
            taskInfo = outstandingTasks.remove(taskId);
        }
        return new TaskState("aborted", completed, started, aborted, failed, created, taskInfo);
    }

    private String getTasksDescription(TaskState taskState) {
        String tasksStatus = String.format("task %s %d / %d|%d|%d / %d", taskState.state, taskState.started,
                taskState.completed, taskState.failed, taskState.aborted, taskState.created);
        return tasksStatus;
    }

    private String getRewardDescription(JobResult result) {
        if (result.reward == null) {
            return String.format("<null> (%s)", result.error);
        }
        return String.format("%s", result.reward);
    }
}
