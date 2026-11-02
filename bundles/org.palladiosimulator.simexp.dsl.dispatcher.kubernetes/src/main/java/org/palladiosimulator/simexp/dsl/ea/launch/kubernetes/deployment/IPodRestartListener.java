package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.deployment;

public interface IPodRestartListener {
    void onRestart(String nodeName, String podName, String reason, int restartCount);
}
