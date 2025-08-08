package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator.QualityMeasurements;
import org.palladiosimulator.simexp.dsl.ea.api.IQualityAttributeProvider;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.IResultLogger;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.JobResult;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.JobResult.Status;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class QualityAttributeProvider implements IQualityAttributeProvider, IResultLogger {
    private final Map<List<OptimizableValue<?>>, QualityMeasurements> qualityMeasurementMap;

    public QualityAttributeProvider() {
        this.qualityMeasurementMap = new HashMap<>();
    }

    @Override
    public synchronized QualityMeasurements getQualityMeasurements(List<OptimizableValue<?>> optimizableValues) {
        QualityMeasurements qualityMeasurements = qualityMeasurementMap.get(optimizableValues);
        return qualityMeasurements;
    }

    @Override
    public void log(List<OptimizableValue<?>> optimizableValues, JobResult result) {
        if (result.status != Status.COMPLETE) {
            return;
        }
        synchronized (this) {
            qualityMeasurementMap.put(optimizableValues, result.qualityMeasurements);
        }
    }

    @Override
    public synchronized void dispose() {
        qualityMeasurementMap.clear();
    }
}
