package org.palladiosimulator.simexp.dsl.dispatcher.kubernetes.task;

import java.io.IOException;

public interface ITaskCreator {
    void createTasks() throws IOException;
}
