package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.result.json;

import java.util.List;

import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.JobResult;

public class TaskResult {
    public static class OptimizableParam<V> {
        public final String name;
        public final V value;

        public OptimizableParam(String name, V value) {
            this.name = name;
            this.value = value;
        }
    }

    public final List<OptimizableParam<?>> optimizables;

    public final JobResult result;

    public TaskResult(List<OptimizableParam<?>> optimizablePairs, JobResult result) {
        this.optimizables = optimizablePairs;
        this.result = result;
    }
}
