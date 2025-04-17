package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task;

import java.util.List;

import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public interface IResultLogger {
    void log(List<OptimizableValue<?>> optimizableValues, JobResult result);

    void dispose();
}
