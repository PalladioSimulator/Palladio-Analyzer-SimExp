package org.palladiosimulator.simexp.dsl.ea.pareto;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;

import org.palladiosimulator.simexp.dsl.ea.api.IQualityAttributeProvider;
import org.palladiosimulator.simexp.dsl.ea.api.IndividualParetoResult;
import org.palladiosimulator.simexp.dsl.ea.api.IndividualResult;
import org.palladiosimulator.simexp.dsl.ea.pareto.impl.AverageProvider;
import org.palladiosimulator.simexp.dsl.ea.pareto.impl.CachingAverageProvider;
import org.palladiosimulator.simexp.dsl.ea.pareto.impl.IAverageProvider;
import org.palladiosimulator.simexp.dsl.ea.pareto.impl.ParetoSetCollector;
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

    public List<IndividualParetoResult> buildParetoFront(List<IndividualResult> population) {
        final IAverageProvider averageProvider = new CachingAverageProvider(
                new AverageProvider(qualityAttributeProvider));
        Function<String, Comparator<Double>> comparatorFactory = qualityAttributeProvider.getComparatorFactory();
        Collector<IndividualResult, ?, ISeq<IndividualResult>> moeaCollector = ParetoSetCollector
            .create(precisionProvider, averageProvider, comparatorFactory, Optimize.MINIMUM);
        ISeq<IndividualResult> phenotypes = population.stream()
            .collect(moeaCollector);
        List<IndividualParetoResult> paretoFront = phenotypes.stream()
            .map(each -> toIndividualParetoResult(each, averageProvider))
            .toList();
        return paretoFront;
    }

    private IndividualParetoResult toIndividualParetoResult(IndividualResult result, IAverageProvider averageProvider) {
        Optional<Map<String, Double>> optionalAverages = averageProvider.getAverages(result);
        Map<String, Double> averages = optionalAverages.orElse(Collections.emptyMap());
        return new IndividualParetoResult(result, averages);
    }
}
