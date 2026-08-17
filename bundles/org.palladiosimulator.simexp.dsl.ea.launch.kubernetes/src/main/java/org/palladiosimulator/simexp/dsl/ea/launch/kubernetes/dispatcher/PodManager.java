package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.dispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.deployment.IPodRestartListener;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.ITaskConsumer;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.JobResult;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.JobResult.Status;

public class PodManager implements IPodRestartListener, ITaskConsumer {
    private static final Logger LOGGER = Logger.getLogger(PodManager.class);

    private static class PodInfo {
        private final Set<String> tasks;

        private int restartCount = 0;

        public PodInfo() {
            this.tasks = new HashSet<>();
        }

        public void addTask(String taskId) {
            tasks.add(taskId);
        }

        public void removeTask(String taskId) {
            tasks.remove(taskId);
        }

        public List<String> popTasks() {
            ArrayList<String> result = new ArrayList<>(tasks);
            tasks.clear();
            return result;
        }

        public List<String> copyTasks() {
            return new ArrayList<>(tasks);
        }

        public int getRestartCount() {
            return restartCount;
        }

        public void setRestartCount(int restartCount) {
            this.restartCount = restartCount;
        }
    }

    private final List<ITaskConsumer> taskConsumers;
    private final Map<String, PodInfo> podInfos;
    private final ClassLoader classloader;

    public PodManager(ClassLoader classloader) {
        this.podInfos = new HashMap<>();
        this.taskConsumers = new ArrayList<>();
        this.classloader = classloader;
    }

    public synchronized void registerTaskConsumer(ITaskConsumer taskConsumer) {
        taskConsumers.add(taskConsumer);
    }

    @Override
    public void onRestart(String nodeName, String podName, String reason, int restartCount) {
        final List<String> tasks;
        final boolean newRestart;
        synchronized (this) {
            PodInfo podInfo = podInfos.get(podName);
            if (podInfo == null) {
                tasks = null;
                newRestart = false;
            } else {
                if (restartCount > podInfo.getRestartCount()) {
                    tasks = podInfo.popTasks();
                    podInfo.setRestartCount(restartCount);
                    newRestart = true;
                } else {
                    tasks = podInfo.copyTasks();
                    newRestart = false;
                }
            }
        }
        ClassLoader oldContextClassLoader = Thread.currentThread()
            .getContextClassLoader();
        Thread.currentThread()
            .setContextClassLoader(classloader);
        try {
            if (newRestart) {
                LOGGER.warn(String.format("handle restart %d of pod %s", restartCount, podName));
                handleAffectedTasks(nodeName, podName, reason, tasks);
            } else {
                LOGGER.warn(String.format("ignore restart %d of pod %s", restartCount, podName));
            }
        } finally {
            Thread.currentThread()
                .setContextClassLoader(oldContextClassLoader);
        }
    }

    private void handleAffectedTasks(String nodeName, String podName, String reason, List<String> tasks) {
        for (String taskId : tasks) {
            LOGGER.warn(String.format("abort task: %s", taskId));
            JobResult result = new JobResult();
            result.id = taskId;
            result.status = Status.ABORT;
            result.executor_id = String.format("%s:default.%s", nodeName, podName);
            result.error = reason;
            notifyConsumers(taskId, result);
        }
    }

    @Override
    public void taskStarted(String taskId, JobResult result) {
        String podName = getPodName(result.executor_id);

        synchronized (this) {
            PodInfo podInfo = podInfos.get(podName);
            if (podInfo == null) {
                podInfo = new PodInfo();
                podInfos.put(podName, podInfo);
            }
            podInfo.addTask(taskId);
        }
    }

    @Override
    public void taskCompleted(String taskId, JobResult result) {
        String podName = getPodName(result.executor_id);

        synchronized (this) {
            PodInfo podInfo = podInfos.get(podName);
            if (podInfo == null) {
                podInfo = new PodInfo();
                podInfos.put(podName, podInfo);
            }
            podInfo.removeTask(taskId);
        }
    }

    @Override
    public void taskAborted(String taskId, JobResult result) {
    }

    private void notifyConsumers(String taskId, JobResult result) {
        final List<ITaskConsumer> consumers;
        synchronized (this) {
            consumers = new ArrayList<>(taskConsumers);
        }
        for (ITaskConsumer consumer : consumers) {
            consumer.taskAborted(taskId, result);
        }
    }

    String getPodName(String executor_id) {
        String[] tokens = executor_id.split("\\.");
        return tokens[1];
    }
}
