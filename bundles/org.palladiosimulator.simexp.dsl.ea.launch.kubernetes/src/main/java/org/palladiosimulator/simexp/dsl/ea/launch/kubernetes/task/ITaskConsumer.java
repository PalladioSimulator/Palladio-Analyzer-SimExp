package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task;

public interface ITaskConsumer {
    void taskCompleted(String answerId, JobResult result);
}
