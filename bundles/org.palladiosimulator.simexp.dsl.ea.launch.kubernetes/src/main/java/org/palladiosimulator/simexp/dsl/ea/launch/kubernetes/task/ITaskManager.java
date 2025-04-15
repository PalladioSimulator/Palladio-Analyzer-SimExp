package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task;

import java.util.Optional;

import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.concurrent.SettableFutureTask;

public interface ITaskManager {
    void newTask(String taskId, SettableFutureTask<Optional<Double>> task);
}
