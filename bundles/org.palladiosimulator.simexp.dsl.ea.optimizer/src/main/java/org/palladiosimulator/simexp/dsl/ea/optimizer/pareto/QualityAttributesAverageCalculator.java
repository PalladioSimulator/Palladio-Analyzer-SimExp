package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator.QualityMeasurements;
import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator.Run;
import org.palladiosimulator.simexp.dsl.ea.api.IQualityAttributeProvider;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class QualityAttributesAverageCalculator {
    private final IQualityAttributeProvider qualityAttributeProvider;

    public QualityAttributesAverageCalculator(IQualityAttributeProvider qualityAttributeProvider) {
        this.qualityAttributeProvider = qualityAttributeProvider;
    }

    public Map<String, Double> calculateAverages(List<OptimizableValue<?>> optimizableValues) {
        QualityMeasurements qualityMeasurements = qualityAttributeProvider.getQualityMeasurements(optimizableValues);
        Map<String, List<Double>> values = extractValues(qualityMeasurements);

        Map<String, Double> averages = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : values.entrySet()) {
            List<Double> currentValues = entry.getValue();
            double average = currentValues.stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(Double.NaN);
            averages.put(entry.getKey(), average);
        }

        return averages;
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