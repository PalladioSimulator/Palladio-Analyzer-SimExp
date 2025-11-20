package org.palladiosimulator.simexp.dsl.ea.pareto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.palladiosimulator.simexp.dsl.ea.api.IQualityAttributeProvider;
import org.palladiosimulator.simexp.dsl.ea.api.IndividualResult;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class AverageProvider implements IAverageProvider {
    private final QualityAttributesAverageCalculator qualityAttributesAverageCalculator;

    public AverageProvider(IQualityAttributeProvider qualityAttributeProvider) {
        this.qualityAttributesAverageCalculator = new QualityAttributesAverageCalculator(qualityAttributeProvider);
    }

    @Override
    public Optional<Map<String, Double>> getAverages(IndividualResult individualResult) {
        List<OptimizableValue<?>> optimizableValues = individualResult.getOptimizableValues();
        Optional<Map<String, Double>> averages = qualityAttributesAverageCalculator
            .calculateAverages(optimizableValues);
        return averages;
    }
}
