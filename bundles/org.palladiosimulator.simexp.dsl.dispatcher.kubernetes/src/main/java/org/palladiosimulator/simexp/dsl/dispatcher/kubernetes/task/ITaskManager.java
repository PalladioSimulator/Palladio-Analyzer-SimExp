package org.palladiosimulator.simexp.dsl.dispatcher.kubernetes.task;

import java.util.List;
import java.util.Optional;

import org.palladiosimulator.simexp.dsl.dispatcher.kubernetes.concurrent.SettableFutureTask;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public interface ITaskManager {
    void newTask(String taskId, SettableFutureTask<Optional<Double>> task, List<OptimizableValue<?>> optimizableValues);
}
