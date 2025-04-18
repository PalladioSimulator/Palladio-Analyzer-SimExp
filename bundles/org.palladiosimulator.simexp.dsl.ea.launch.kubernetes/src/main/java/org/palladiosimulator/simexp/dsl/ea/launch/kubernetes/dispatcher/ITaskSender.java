package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.dispatcher;

import java.io.IOException;

import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.JobTask;

public interface ITaskSender {
    void sendTask(JobTask task, String description) throws IOException;
}
