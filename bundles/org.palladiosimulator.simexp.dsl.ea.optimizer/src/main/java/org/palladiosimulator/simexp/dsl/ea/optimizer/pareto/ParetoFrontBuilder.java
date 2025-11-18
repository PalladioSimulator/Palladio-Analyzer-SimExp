package org.palladiosimulator.simexp.dsl.ea.optimizer.pareto;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.EAResult.IndividualResult;
import org.palladiosimulator.simexp.dsl.ea.api.IQualityAttributeProvider;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.ITranscoder;
import org.palladiosimulator.simexp.dsl.smodel.api.IPrecisionProvider;

import io.jenetics.Gene;
import io.jenetics.Optimize;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.ISeq;

public class ParetoFrontBuilder<G extends Gene<?, G>> {
    private final static Logger LOGGER = Logger.getLogger(ParetoFrontBuilder.class);

    private final ITranscoder<G> normalizer;
    private final IQualityAttributeProvider qualityAttributeProvider;
    private final IPrecisionProvider precisionProvider;

    public ParetoFrontBuilder(ITranscoder<G> normalizer, IQualityAttributeProvider qualityAttributeProvider,
            IPrecisionProvider precisionProvider) {
        this.normalizer = normalizer;
        this.qualityAttributeProvider = qualityAttributeProvider;
        this.precisionProvider = precisionProvider;
    }

    public List<IndividualResult> buildParetoFront(EvolutionResult<G, Double> result) {
        IAverageProvider averageProvider = new AverageProvider(qualityAttributeProvider);
        averageProvider = new CachingAverageProvider(averageProvider);
        Function<String, Comparator<Double>> comparatorFactory = qualityAttributeProvider.getComparatorFactory();
        Collector<IndividualResult, ?, ISeq<IndividualResult>> moeaCollector = ParetoSetCollector
            .create(precisionProvider, averageProvider, comparatorFactory, Optimize.MINIMUM);
        List<IndividualResult> population = result.population()
            .stream()
            .map(p -> new IndividualResult(p.fitness(), normalizer.toOptimizableValues(p.genotype())))
            .toList();
        LOGGER.info("building pareto front");
        ISeq<IndividualResult> phenotypes = population.stream()
            .collect(moeaCollector);
        List<IndividualResult> paretoFront = phenotypes.stream()
            .toList();
        return paretoFront;
    }
}
