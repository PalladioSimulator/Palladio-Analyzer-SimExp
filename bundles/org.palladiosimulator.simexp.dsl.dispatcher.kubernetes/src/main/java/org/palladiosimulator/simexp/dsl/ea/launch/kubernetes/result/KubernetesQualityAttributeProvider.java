package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.result;

import java.util.List;

import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.IResultHandler;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.JobResult;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.JobResult.Status;
import org.palladiosimulator.simexp.dsl.ea.launch.quality.BaseQualityAttributeProvider;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class KubernetesQualityAttributeProvider extends BaseQualityAttributeProvider implements IResultHandler {
    @Override
    public void process(List<OptimizableValue<?>> optimizableValues, JobResult result) {
        if (result.status == Status.COMPLETE) {
            put(optimizableValues, result.qualityMeasurements);
        } else {
            put(optimizableValues, null);
        }
    }
}
