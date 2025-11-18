package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;

import org.palladiosimulator.simexp.dsl.ea.api.IQualityAttributeProvider;
import org.palladiosimulator.simexp.dsl.ea.api.IndividualResult;
import org.palladiosimulator.simexp.dsl.smodel.api.IPrecisionProvider;

import io.jenetics.Optimize;
import io.jenetics.util.ISeq;

public class ParetoFrontBuilder {
    private final IQualityAttributeProvider qualityAttributeProvider;
    private final IPrecisionProvider precisionProvider;

    public ParetoFrontBuilder(IQualityAttributeProvider qualityAttributeProvider,
            IPrecisionProvider precisionProvider) {
        this.qualityAttributeProvider = qualityAttributeProvider;
        this.precisionProvider = precisionProvider;
    }

    public List<IndividualResult> buildParetoFront(List<IndividualResult> population) {
        IAverageProvider averageProvider = new AverageProvider(qualityAttributeProvider);
        averageProvider = new CachingAverageProvider(averageProvider);
        Function<String, Comparator<Double>> comparatorFactory = qualityAttributeProvider.getComparatorFactory();
        Collector<IndividualResult, ?, ISeq<IndividualResult>> moeaCollector = ParetoSetCollector
            .create(precisionProvider, averageProvider, comparatorFactory, Optimize.MINIMUM);
        ISeq<IndividualResult> phenotypes = population.stream()
            .collect(moeaCollector);
        List<IndividualResult> paretoFront = phenotypes.stream()
            .toList();
        return paretoFront;
    }
}
