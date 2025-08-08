package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
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
    private final Map<String, String> startedTasks;
    private final Set<String> completedTasks;
    private final Set<String> abortedTasks;
    private final Set<String> failedTasks;

    private int createdCount = 0;

    public TaskManager(IResultLogger resultLogger) {
        this.resultLogger = resultLogger;
        this.outstandingTasks = new HashMap<>();
        this.startedTasks = new HashMap<>();
        this.completedTasks = new HashSet<>();
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
        public final List<String> remaining;

        public TaskState(String state, int completed, int started, int aborted, int failed, int created,
                TaskInfo taskInfo, List<String> remaining) {
            this.state = state;
            this.completed = completed;
            this.started = started;
            this.aborted = aborted;
            this.failed = failed;
            this.created = created;
            this.taskInfo = taskInfo;
            this.remaining = Collections.unmodifiableList(remaining);
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
        LOGGER.info(String.format("remaining: %s", listRemainingTaskContainer(taskState.remaining)));
    }

    TaskState onTaskCreated(String taskId, SettableFutureTask<Optional<Double>> task,
            List<OptimizableValue<?>> optimizableValues) {
        final int completed;
        final int started;
        final int aborted;
        final int failed;
        final int created;
        final List<String> remaining;
        synchronized (this) {
            createdCount++;
            outstandingTasks.put(taskId, new TaskInfo(task, optimizableValues));
            started = startedTasks.size();
            completed = completedTasks.size();
            aborted = abortedTasks.size();
            failed = failedTasks.size();
            created = createdCount;
            remaining = new ArrayList<>(startedTasks.values());
        }
        return new TaskState("created", completed, started, aborted, failed, created, null, remaining);
    }

    @Override
    public void taskStarted(String taskId, JobResult result) {
        TaskState taskState = onTaskStarted(taskId, result);
        String tasksDescription = getTasksDescription(taskState);
        LOGGER.info(String.format("%s [%s] by %s (redelivered: %s %d)", tasksDescription, result.id, result.executor_id,
                result.redelivered, result.delivery_count));
        LOGGER.info(String.format("remaining: %s", listRemainingTaskContainer(taskState.remaining)));
    }

    TaskState onTaskStarted(String taskId, JobResult result) {
        final int completed;
        final int started;
        final int aborted;
        final int failed;
        final int created;
        final List<String> remaining;
        synchronized (this) {
            if (!completedTasks.contains(taskId)) {
                startedTasks.put(taskId, result.executor_id);
            }
            started = startedTasks.size();
            completed = completedTasks.size();
            aborted = abortedTasks.size();
            failed = failedTasks.size();
            created = createdCount;
            remaining = new ArrayList<>(startedTasks.values());
        }
        return new TaskState("started", completed, started, aborted, failed, created, null, remaining);
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
        LOGGER.info(String.format("remaining: %s", listRemainingTaskContainer(taskState.remaining)));
    }

    TaskState onTaskCompleted(String taskId, JobResult result) {
        final int completed;
        final int started;
        final int aborted;
        final int failed;
        final int created;
        final List<String> remaining;
        final TaskInfo taskInfo;
        final String statusString;
        synchronized (this) {
            startedTasks.remove(taskId);
            started = startedTasks.size();
            taskInfo = outstandingTasks.remove(taskId);
            if (result.reward != null) {
                if (taskInfo != null) {
                    statusString = "completed";
                    completedTasks.add(taskId);
                } else {
                    statusString = "completed after abort";
                }
            } else {
                statusString = "failed";
                if (taskInfo != null) {
                    failedTasks.add(taskId);
                }
            }
            completed = completedTasks.size();
            aborted = abortedTasks.size();
            failed = failedTasks.size();
            created = createdCount;
            remaining = new ArrayList<>(startedTasks.values());
        }
        if (taskInfo != null) {
            resultLogger.log(taskInfo.optimizableValues, result);
            SettableFutureTask<Optional<Double>> future = taskInfo.future;
            if (result.reward == null) {
                future.setResult(Optional.empty());
            } else {
                future.setResult(Optional.of(result.reward));
            }
        }
        return new TaskState(statusString, completed, started, aborted, failed, created, taskInfo, remaining);
    }

    @Override
    public void taskAborted(String taskId, JobResult result) {
        TaskState taskState = onTaskAborted(taskId, result);
        if (taskState.taskInfo != null) {
            String description = getTasksDescription(taskState);
            LOGGER.warn(String.format("%s [%s] by %s (%s)", description, taskId, result.executor_id, result.error));
        } else {
            LOGGER.warn(String.format("aborted task [%s] already completed/aborted", taskId));
        }
        LOGGER.info(String.format("remaining: %s", listRemainingTaskContainer(taskState.remaining)));
    }

    TaskState onTaskAborted(String taskId, JobResult result) {
        final int completed;
        final int started;
        final int aborted;
        final int failed;
        final int created;
        final List<String> remaining;
        final TaskInfo taskInfo;
        synchronized (this) {
            boolean processed = !startedTasks.containsKey(taskId);
            startedTasks.remove(taskId);
            started = startedTasks.size();
            completed = completedTasks.size();
            if (!processed) {
                abortedTasks.add(taskId);
            }
            aborted = abortedTasks.size();
            failed = failedTasks.size();
            created = createdCount;
            remaining = new ArrayList<>(startedTasks.values());
            taskInfo = outstandingTasks.remove(taskId);
        }
        if (taskInfo != null) {
            resultLogger.log(taskInfo.optimizableValues, result);
            SettableFutureTask<Optional<Double>> future = taskInfo.future;
            future.setResult(Optional.empty());
        }
        return new TaskState("aborted", completed, started, aborted, failed, created, taskInfo, remaining);
    }

    private String getTasksDescription(TaskState taskState) {
        int done = taskState.completed + taskState.failed + taskState.aborted;
        String tasksStatus = String.format("task %s %d / %d(%d|%d|%d) / %d", taskState.state, taskState.started, done,
                taskState.completed, taskState.failed, taskState.aborted, taskState.created);
        return tasksStatus;
    }

    private String listRemainingTaskContainer(List<String> remaining) {
        final List<String> toReport;
        final String info;
        if (remaining.size() <= 3) {
            toReport = remaining;
            info = "";
        } else {
            toReport = remaining.subList(0, 3);
            info = " ...";
        }

        List<String> containerIds = toReport.stream()
            .filter(x -> x != null)
            .map(e -> e.substring(e.lastIndexOf(".") + 1))
            .toList();
        return String.format("%s%s", StringUtils.join(containerIds, ","), info);
    }

    private String getRewardDescription(JobResult result) {
        if (result.reward == null) {
            return String.format("<null> (%s)", result.error);
        }
        return String.format("%s", result.reward);
    }
}
