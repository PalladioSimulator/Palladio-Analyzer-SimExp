package org.palladiosimulator.simexp.dsl.ea.api;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator.QualityMeasurements;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public interface IQualityAttributeProvider {
    Optional<QualityMeasurements> getQualityMeasurements(List<OptimizableValue<?>> optimizableValues);

    Function<String, Comparator<Double>> getComparatorFactory();
}
