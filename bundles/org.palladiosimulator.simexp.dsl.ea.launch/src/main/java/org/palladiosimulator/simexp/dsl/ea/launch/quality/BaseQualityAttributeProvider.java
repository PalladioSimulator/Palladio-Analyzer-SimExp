package org.palladiosimulator.simexp.dsl.ea.launch.quality;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator.QualityMeasurements;
import org.palladiosimulator.simexp.dsl.ea.api.IQualityAttributeProvider;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class BaseQualityAttributeProvider implements IQualityAttributeProvider {
    private final Map<List<OptimizableValue<?>>, QualityMeasurements> qualityMeasurementMap;

    public BaseQualityAttributeProvider() {
        this.qualityMeasurementMap = new HashMap<>();
    }

    protected synchronized void put(List<OptimizableValue<?>> optimizableValues, QualityMeasurements measurements) {
        qualityMeasurementMap.put(optimizableValues, measurements);
    }

    @Override
    public synchronized Optional<QualityMeasurements> getQualityMeasurements(
            List<OptimizableValue<?>> optimizableValues) {
        if (qualityMeasurementMap.containsKey(optimizableValues)) {
            QualityMeasurements qualityMeasurements = qualityMeasurementMap.get(optimizableValues);
            return Optional.ofNullable(qualityMeasurements);
        }
        return null;
    }

    public synchronized void dispose() {
        qualityMeasurementMap.clear();
    }

    @Override
    public Function<String, Comparator<Double>> getComparatorFactory() {
        // TODO:
        return s -> Double::compare;
    }
}
