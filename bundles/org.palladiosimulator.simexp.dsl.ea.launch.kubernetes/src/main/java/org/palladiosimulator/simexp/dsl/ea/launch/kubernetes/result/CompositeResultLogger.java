package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.result;

import java.util.Collections;
import java.util.List;

import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.IResultLogger;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.JobResult;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class CompositeResultLogger implements IResultLogger {
    private final List<IResultLogger> loggers;

    public CompositeResultLogger(List<IResultLogger> loggers) {
        this.loggers = Collections.unmodifiableList(loggers);
    }

    @Override
    public void log(List<OptimizableValue<?>> optimizableValues, JobResult result) {
        loggers.stream()
            .forEach(l -> l.log(optimizableValues, result));
    }

    @Override
    public void dispose() {
        loggers.stream()
            .forEach(l -> l.dispose());
    }
}
