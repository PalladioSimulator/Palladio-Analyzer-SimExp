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
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.deployment.IPodRestartListener;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class TaskManager implements ITaskManager, ITaskConsumer, IPodRestartListener {
    private static final Logger LOGGER = Logger.getLogger(TaskManager.class);

    private static class TaskInfo {
        public final SettableFutureTask<Optional<Double>> future;
        public final List<OptimizableValue<?>> optimizableValues;

        public TaskInfo(SettableFutureTask<Optional<Double>> future, List<OptimizableValue<?>> optimizableValues) {
            this.future = future;
            this.optimizableValues = new ArrayList<>(optimizableValues);
        }
    }

    private static class PodInfo {
        private final Set<String> tasks;

        public PodInfo() {
            this.tasks = new HashSet<>();
        }

        public void addTask(String taskId) {
            getTasks().add(taskId);
        }

        public void removeTask(String taskId) {
            getTasks().remove(taskId);
        }

        public Set<String> getTasks() {
            return tasks;
        }
    }

    private final IResultLogger resultLogger;
    private final Map<String, TaskInfo> outstandingTasks;
    private final Set<String> startedTasks;
    private final Map<String, PodInfo> pods;

    private int receivedCount = 0;
    private int taskCount = 0;

    public TaskManager(IResultLogger resultLogger) {
        this.resultLogger = resultLogger;
        this.outstandingTasks = new HashMap<>();
        this.startedTasks = new HashSet<>();
        this.pods = new HashMap<>();
    }

    @Override
    public void newTask(String taskId, SettableFutureTask<Optional<Double>> task,
            List<OptimizableValue<?>> optimizableValues) {
        final int received;
        final int started;
        final int created;
        synchronized (this) {
            taskCount++;
            outstandingTasks.put(taskId, new TaskInfo(task, optimizableValues));
            received = receivedCount;
            started = startedTasks.size();
            created = taskCount;
        }
        OptimizableValueToString optimizableValueToString = new OptimizableValueToString();
        String description = optimizableValueToString.asString(optimizableValues);
        String tasksStatus = getTasksStatus("created", received, started, created);
        LOGGER.info(String.format("%s [%s]: %s", tasksStatus, taskId, description));
    }

    @Override
    public void taskStarted(String taskId, JobResult result) {
        String podName = getPodName(result.executor_id);
        final int received;
        final int started;
        final int created;
        synchronized (this) {
            startedTasks.add(taskId);
            PodInfo podInfo = pods.get(podName);
            if (podInfo == null) {
                podInfo = new PodInfo();
                pods.put(podName, podInfo);
            }
            podInfo.addTask(taskId);
            received = receivedCount;
            started = startedTasks.size();
            created = taskCount;
        }
        String tasksStatus = getTasksStatus("started", received, started, created);
        LOGGER.info(String.format("%s [%s] by %s (redelivered: %s)", tasksStatus, result.id, result.executor_id,
                result.redelivered));
    }

    String getPodName(String executor_id) {
        // node02:default.simexp-c6f6d95f4-8b9f8
        String[] tokens = executor_id.split("\\.");
        return tokens[1];
    }

    @Override
    public void taskCompleted(String taskId, JobResult result) {
        String podName = getPodName(result.executor_id);
        final int received;
        final int started;
        final int created;
        final TaskInfo taskInfo;
        synchronized (this) {
            startedTasks.remove(taskId);
            PodInfo podInfo = pods.get(podName);
            if (podInfo != null) {
                podInfo.removeTask(taskId);
            }
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

    @Override
    public void onRestart(String podName, Reason reason) {
        final int received;
        final int started;
        final int created;
        final Set<String> tasks;
        synchronized (this) {
            PodInfo podInfo = pods.get(podName);
            if (podInfo != null) {
                tasks = podInfo.getTasks();
                podInfo.getTasks()
                    .clear();
            } else {
                tasks = Collections.emptySet();
            }
            received = receivedCount;
            created = taskCount;
            started = startedTasks.size();
        }
        String tasksStatus = getTasksStatus("pod restart", received, started, created);
        LOGGER.info(String.format("%s [%s] by %s (%s)", tasksStatus, StringUtils.join(tasks, ","), podName, reason));
    }
}
