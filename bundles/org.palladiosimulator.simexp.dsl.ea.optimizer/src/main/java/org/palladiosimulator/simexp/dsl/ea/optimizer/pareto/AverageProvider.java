package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import java.util.List;
import java.util.Map;

import org.palladiosimulator.simexp.dsl.ea.api.IQualityAttributeProvider;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.ITranscoder;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;

public class AverageProvider<G extends Gene<?, G>> implements IAverageProvider<G> {
    private final ITranscoder<G> normalizer;
    private final QualityAttributesAverageCalculator qualityAttributesAverageCalculator;

    public AverageProvider(ITranscoder<G> normalizer, IQualityAttributeProvider qualityAttributeProvider) {
        this.normalizer = normalizer;
        this.qualityAttributesAverageCalculator = new QualityAttributesAverageCalculator(qualityAttributeProvider);
    }

    @Override
    public Map<String, Double> getAverages(Phenotype<G, Double> phenotype) {
        Genotype<G> genotype = phenotype.genotype();
        List<OptimizableValue<?>> optimizableValues = normalizer.toOptimizableValues(genotype);
        Map<String, Double> averages = qualityAttributesAverageCalculator.calculateAverages(optimizableValues);
        return averages;
    }
}
