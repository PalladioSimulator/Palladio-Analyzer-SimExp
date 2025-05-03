package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task;

import java.util.List;
import java.util.Optional;

import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.concurrent.SettableFutureTask;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public interface ITaskManager {
    void newTask(String taskId, SettableFutureTask<Optional<Double>> task, List<OptimizableValue<?>> optimizableValues);
}
