package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.result;

import java.util.Collections;
import java.util.List;

import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.IResultHandler;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.JobResult;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class CompositeResultHandler implements IResultHandler {
    private final List<IResultHandler> handlers;

    public CompositeResultHandler(List<IResultHandler> handlers) {
        this.handlers = Collections.unmodifiableList(handlers);
    }

    @Override
    public void process(List<OptimizableValue<?>> optimizableValues, JobResult result) {
        handlers.stream()
            .forEach(h -> h.process(optimizableValues, result));
    }

    @Override
    public void dispose() {
        handlers.stream()
            .forEach(h -> h.dispose());
    }
}
