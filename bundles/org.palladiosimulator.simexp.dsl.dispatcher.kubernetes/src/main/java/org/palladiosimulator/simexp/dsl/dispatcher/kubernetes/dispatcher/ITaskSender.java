package org.palladiosimulator.simexp.dsl.dispatcher.kubernetes.dispatcher;

import java.io.IOException;

import org.palladiosimulator.simexp.dsl.dispatcher.kubernetes.task.JobTask;

public interface ITaskSender {
    void sendTask(JobTask task) throws IOException;
}
