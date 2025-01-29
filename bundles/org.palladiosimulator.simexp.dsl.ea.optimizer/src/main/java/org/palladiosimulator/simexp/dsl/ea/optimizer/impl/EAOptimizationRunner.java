package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import static io.jenetics.engine.Limits.bySteadyFitness;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.EAResult;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.smodel.OptimizableNormalizer;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;

public class EAOptimizationRunner {

    private final static Logger LOGGER = Logger.getLogger(EAOptimizer.class);

    public EAResult runOptimization(IEAEvolutionStatusReceiver evolutionStatusReceiver,
            OptimizableNormalizer normalizer, final Engine<BitGene, Double> engine) {
        final EvolutionStatistics<Double, ?> statistics = EvolutionStatistics.ofNumber();
        EAReporter reporter = new EAReporter(evolutionStatusReceiver, normalizer);

        EvolutionResult<BitGene, Double> result = engine.stream()
            .limit(bySteadyFitness(7))
            .limit(10)
            .peek(statistics)
            .peek(reporter)
            .collect(EvolutionResult.toBestEvolutionResult());
        LOGGER.info("EA finished...");

        Double bestFitness = result.bestFitness();
        Phenotype<BitGene, Double> bestPhenotype = result.bestPhenotype();
        Genotype<BitGene> genotype = bestPhenotype.genotype();
        List<SmodelBitChromosome> bestChromosomes = genotype.stream()
            .map(g -> g.as(SmodelBitChromosome.class))
            .collect(Collectors.toList());
        List<OptimizableValue<?>> bestOptimizables = normalizer.toOptimizableValues(bestChromosomes);
        List<List<OptimizableValue<?>>> bestOptimizablesList = new ArrayList<>();
        bestOptimizablesList.add(bestOptimizables);
        // TODO: add remaining, too

        LOGGER.info(statistics);

        return new EAResult(bestFitness, bestOptimizablesList);
    }

}
