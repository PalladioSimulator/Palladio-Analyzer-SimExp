package org.palladiosimulator.simexp.dsl.dispatcher.kubernetes.task;

import java.util.List;

import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public interface IResultHandler {
    void process(List<OptimizableValue<?>> optimizableValues, JobResult result);

    void dispose();
}
