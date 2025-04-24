package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.deployment;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.deployment.IPodRestartListener.Reason;

import io.fabric8.kubernetes.api.model.ContainerState;
import io.fabric8.kubernetes.api.model.ContainerStateRunning;
import io.fabric8.kubernetes.api.model.ContainerStateTerminated;
import io.fabric8.kubernetes.api.model.ContainerStateWaiting;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;

public class PodRestartObserver implements Watcher<Pod>, AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(PodRestartObserver.class);

    private final ClassLoader classloader;
    private final Watch watch;
    private final List<IPodRestartListener> listeners = new ArrayList<>();

    public PodRestartObserver(ClassLoader classloader, KubernetesClient client) {
        this.classloader = classloader;
        LOGGER.info("pod restart observer start");
        watch = client.pods()
            .inNamespace("default")
            .watch(this);
    }

    public synchronized void addListener(IPodRestartListener listener) {
        listeners.add(listener);
    }

    public synchronized void removeListener(IPodRestartListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void close() {
        LOGGER.info("pod restart observer close");
        watch.close();
    }

    @Override
    public void eventReceived(Action action, Pod pod) {
        ClassLoader oldContextClassLoader = Thread.currentThread()
            .getContextClassLoader();
        Thread.currentThread()
            .setContextClassLoader(classloader);
        try {

            String podName = pod.getMetadata()
                .getName();
            String nodeName = pod.getSpec()
                .getNodeName();
            PodStatus podStatus = pod.getStatus();
            List<ContainerStatus> statuses = podStatus.getContainerStatuses();
            LOGGER.warn(String.format("pod %s event: %s status %s(%s) count: %d", podName, action,
                    podStatus.getMessage(), podStatus.getReason(), statuses.size()));
            for (ListIterator<ContainerStatus> it = statuses.listIterator(); it.hasNext();) {
                int index = it.nextIndex();
                ContainerStatus status = it.next();
                checkRestart(nodeName, podName, index, status);
            }
        } finally {
            Thread.currentThread()
                .setContextClassLoader(oldContextClassLoader);
        }
    }

    private void checkRestart(String nodeName, String podName, int index, ContainerStatus status) {
        int restartCount = status.getRestartCount();
        if (restartCount > 0) {
            ContainerState lastState = status.getLastState();
            final Reason reason;
            final String terminatedAt;
            if (lastState != null) {
                ContainerStateTerminated terminated = lastState.getTerminated();
                reason = getReason(terminated);
                terminatedAt = terminated.getFinishedAt();
            } else {
                reason = getReason(null);
                terminatedAt = "n/a";
            }
            String containerName = status.getName();
            String containerState = getContainerState(status);
            LOGGER.warn(String.format("pod %s, container %s restarted (state %s) at %s %d times (index: %d)", podName,
                    containerName, containerState, terminatedAt, restartCount, index));
            notifyListeners(nodeName, podName, reason, restartCount);
        }
    }

    private String getContainerState(ContainerStatus status) {
        ContainerState containerState = status.getState();
        StringBuilder sb = new StringBuilder();
        ContainerStateRunning running = containerState.getRunning();
        if (running != null) {
            sb.append("running");
            sb.append(" since ")
                .append(running.getStartedAt());
        }
        ContainerStateWaiting waiting = containerState.getWaiting();
        if (waiting != null) {
            sb.append(" waiting");
            sb.append(" reason ")
                .append(waiting.getReason());
            sb.append(" message ")
                .append(waiting.getMessage());
        }
        ContainerStateTerminated terminated = containerState.getTerminated();
        if (terminated != null) {
            sb.append(" terminated");
            sb.append(" started ")
                .append(terminated.getStartedAt());
            sb.append(" terminated ")
                .append(terminated.getFinishedAt());
            sb.append(" reason ")
                .append(terminated.getReason());
        }
        return sb.toString();
    }

    private Reason getReason(ContainerStateTerminated terminated) {
        if (terminated != null) {
            if ("OOMKilled".equals(terminated.getReason())) {
                return Reason.OOMKilled;
            }
        }
        return Reason.UNKNOWN;
    }

    @Override
    public void onClose(WatcherException e) {
        LOGGER.error("pod restart observer closed due to error", e);
    }

    private void notifyListeners(String nodeName, String podName, Reason reason, int restartCount) {
        final List<IPodRestartListener> tempListeners;
        synchronized (this) {
            tempListeners = new ArrayList<>(listeners);
        }
        try {
            for (IPodRestartListener listener : tempListeners) {
                listener.onRestart(nodeName, podName, reason, restartCount);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
