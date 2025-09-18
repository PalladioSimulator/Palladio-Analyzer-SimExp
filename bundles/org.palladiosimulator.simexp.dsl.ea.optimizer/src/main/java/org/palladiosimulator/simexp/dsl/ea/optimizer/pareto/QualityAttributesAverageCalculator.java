package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator.QualityMeasurements;
import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator.Run;
import org.palladiosimulator.simexp.dsl.ea.api.IQualityAttributeProvider;
import org.palladiosimulator.simexp.dsl.ea.api.util.OptimizableValueToString;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class QualityAttributesAverageCalculator {
    private final IQualityAttributeProvider qualityAttributeProvider;

    public QualityAttributesAverageCalculator(IQualityAttributeProvider qualityAttributeProvider) {
        this.qualityAttributeProvider = qualityAttributeProvider;
    }

    public Optional<Map<String, Double>> calculateAverages(List<OptimizableValue<?>> optimizableValues) {
        Optional<QualityMeasurements> qualityMeasurements = qualityAttributeProvider
            .getQualityMeasurements(optimizableValues);
        if (qualityMeasurements == null) {
            OptimizableValueToString optimizableValueToString = new OptimizableValueToString();
            String optimizables = optimizableValueToString.asString(optimizableValues);
            throw new RuntimeException(String.format("no quality measurements found for: %s", optimizables));
        }
        if (qualityMeasurements.isEmpty()) {
            return Optional.empty();
        }

        Map<String, List<Double>> values = extractValues(qualityMeasurements.get());

        Map<String, Double> averages = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : values.entrySet()) {
            List<Double> currentValues = entry.getValue();
            double average = currentValues.stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(Double.NaN);
            averages.put(entry.getKey(), average);
        }

        return Optional.of(averages);
    }

    private Map<String, List<Double>> extractValues(QualityMeasurements qualityMeasurement) {
        Map<String, List<Double>> values = new HashMap<>();
        for (Run run : qualityMeasurement.getRuns()) {
            for (Map.Entry<String, List<Double>> entry : run.getQualityAttributes()
                .entrySet()) {
                List<Double> currentValues = values.get(entry.getKey());
                if (currentValues == null) {
                    currentValues = new ArrayList<>();
                }
                currentValues.addAll(entry.getValue());
                values.put(entry.getKey(), currentValues);
            }
        }
        return values;
    }
}