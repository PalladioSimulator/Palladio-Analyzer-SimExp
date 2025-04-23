package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.deployment;

import java.util.List;

import org.apache.log4j.Logger;

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

    public PodRestartObserver(KubernetesClient client) {
        LOGGER.info("pod restart observer start");
        watch = client.pods()
            .inNamespace("default")
            .watch(this);
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
                LOGGER.warn("Pod " + pod.getMetadata()
                    .getName() + " container " + status.getName() + " restarted " + restartCount + " times.");
                ContainerState lastState = status.getLastState();
                if (lastState != null) {
                    ContainerStateTerminated terminated = lastState.getTerminated();
                    if (terminated != null) {
                        LOGGER.warn(
                                "Container " + status.getName() + " last terminated at: " + terminated.getFinishedAt());
                        LOGGER.warn("Exit code: " + terminated.getExitCode());
                        LOGGER.warn("Reason: " + terminated.getReason());
                        LOGGER.warn("Message: " + terminated.getMessage());
                    }
                }
            }
        }
    }

    @Override
    public void onClose(WatcherException e) {
        LOGGER.error("pod restart observer closed due to error", e);
    }
}
