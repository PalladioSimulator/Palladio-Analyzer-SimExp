package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.deployment;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.deployment.IPodRestartListener.Reason;

import io.fabric8.kubernetes.api.model.ContainerState;
import io.fabric8.kubernetes.api.model.ContainerStateTerminated;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;

public class PodRestartObserver implements Watcher<Pod>, AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(PodRestartObserver.class);

    private final Watch watch;
    private final List<IPodRestartListener> listeners = new ArrayList<>();

    public PodRestartObserver(KubernetesClient client) {
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
        PodStatus podStatus = pod.getStatus();
        List<ContainerStatus> statuses = podStatus.getContainerStatuses();
        for (ContainerStatus status : statuses) {
            int restartCount = status.getRestartCount();
            if (restartCount > 0) {
                String podName = pod.getMetadata()
                    .getName();
                LOGGER.warn(
                        "Pod " + podName + " container " + status.getName() + " restarted " + restartCount + " times.");
                ContainerState lastState = status.getLastState();
                final Reason reason;
                if (lastState != null) {
                    ContainerStateTerminated terminated = lastState.getTerminated();
                    /*
                     * if (terminated != null) { LOGGER.warn( "Container " + status.getName() +
                     * " last terminated at: " + terminated.getFinishedAt());
                     * LOGGER.warn("Exit code: " + terminated.getExitCode()); LOGGER.warn("Reason: "
                     * + terminated.getReason()); LOGGER.warn("Message: " +
                     * terminated.getMessage()); }
                     */
                    reason = getReason(terminated);
                } else {
                    reason = getReason(null);
                }
                notifyListeners(podName, reason);
            }
        }
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

    private void notifyListeners(String podName, Reason reason) {
        final List<IPodRestartListener> tempListeners;
        synchronized (this) {
            tempListeners = new ArrayList<>(listeners);
        }
        for (IPodRestartListener listener : tempListeners) {
            listener.onRestart(podName, reason);
        }
    }
}
