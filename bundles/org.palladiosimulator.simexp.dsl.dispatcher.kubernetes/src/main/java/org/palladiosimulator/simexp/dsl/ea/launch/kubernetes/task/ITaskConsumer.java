package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task;

public interface ITaskConsumer {
    void taskStarted(String taskId, JobResult result);

    void taskCompleted(String taskId, JobResult result);

    void taskAborted(String taskId, JobResult result);
}
