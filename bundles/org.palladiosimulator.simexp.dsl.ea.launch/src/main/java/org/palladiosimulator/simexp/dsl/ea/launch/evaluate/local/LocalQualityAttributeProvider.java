package org.palladiosimulator.simexp.dsl.ea.launch.evaluate.local;

import java.util.List;

import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator.QualityMeasurements;
import org.palladiosimulator.simexp.dsl.ea.launch.quality.BaseQualityAttributeProvider;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class LocalQualityAttributeProvider extends BaseQualityAttributeProvider {
    public void add(List<OptimizableValue<?>> optimizableValues, QualityMeasurements measurements) {
        put(optimizableValues, measurements);
    }
}
