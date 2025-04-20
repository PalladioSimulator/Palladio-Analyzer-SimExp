package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task;

public interface ITaskConsumer {
    void taskStarted(String answerId, JobResult result);

    void taskCompleted(String answerId, JobResult result);
}
