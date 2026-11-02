package org.palladiosimulator.simexp.dsl.dispatcher.kubernetes.result.json;

import java.util.Map;

import org.palladiosimulator.simexp.dsl.dispatcher.kubernetes.task.JobResult;

public class TaskResult {
    public final Map<String, Object> optimizables;
    public final JobResult result;

    public TaskResult(Map<String, Object> optimizables, JobResult result) {
        this.optimizables = optimizables;
        this.result = result;
    }
}
