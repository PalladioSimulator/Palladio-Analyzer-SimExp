package org.palladiosimulator.simexp.dsl.ea.api;

import java.util.List;

import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator.QualityMeasurements;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public interface IQualityAttributeProvider {
    QualityMeasurements getQualityMeasurements(List<OptimizableValue<?>> optimizableValues);
}
