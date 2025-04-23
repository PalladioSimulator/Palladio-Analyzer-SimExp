package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.deployment;

public interface IPodRestartListener {
    enum Reason {
        UNKNOWN, OOMKilled;
    }

    void onRestart(String podName, Reason reason);
}
