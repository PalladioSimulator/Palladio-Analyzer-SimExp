package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.deployment;

public interface IPodRestartListener {
    enum Reason {
        UNKNOWN, OOMKilled;
    }

    void onRestart(String nodeName, String podName, Reason reason, int restartCount);
}
