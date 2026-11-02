package org.palladiosimulator.simexp.dsl.dispatcher.kubernetes.deployment;

public interface IPodRestartListener {
    void onRestart(String nodeName, String podName, String reason, int restartCount);
}
