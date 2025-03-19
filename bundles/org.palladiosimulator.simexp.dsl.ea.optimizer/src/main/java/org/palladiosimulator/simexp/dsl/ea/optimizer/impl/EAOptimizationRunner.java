package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.EAResult;
import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.smodel.OptimizableNormalizer;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionStream;
import io.jenetics.engine.Limits;
import io.jenetics.util.ISeq;

public class EAOptimizationRunner {

    private static final int DEFAULT_MAX_GENERATIONS = 100;
    private static final int DEFAULT_STEADY_FITNESS_GENERATION_NUMBER = 7;
    private final static Logger LOGGER = Logger.getLogger(EAOptimizer.class);

    @SuppressWarnings("unchecked")
    public EAResult runOptimization(IEAEvolutionStatusReceiver evolutionStatusReceiver,
            OptimizableNormalizer normalizer, MOEAFitnessFunction fitnessFunction, final Engine<BitGene, Double> engine,
            IEAConfig config) {
        Genotype<BitGene> genotypeInstance = engine.genotypeFactory()
            .newInstance();
        ParetoCompatibleEvolutionStatistics paretoStatistics = new ParetoCompatibleEvolutionStatistics(fitnessFunction,
                genotypeInstance);

        EAReporter reporter = new EAReporter(evolutionStatusReceiver, normalizer);

        EvolutionStream<BitGene, Double> evolutionStream = engine.stream();
        if (config.steadyFitness()
            .isPresent()) {
            evolutionStream = evolutionStream.limit(Limits.byPopulationConvergence(0.0001));
        }
        if (config.maxGenerations()
            .isPresent()) {
            evolutionStream = evolutionStream.limit(Limits.byFixedGeneration(config.maxGenerations()
                .get()));
        }
        ISeq<Phenotype<BitGene, Double>> result = evolutionStream.peek(reporter)
            .peek(paretoStatistics)
            .collect(ParetoSetCollector.create());

        LOGGER.info("EA finished...");
        LOGGER.info(paretoStatistics);

        // all pareto efficient optimizables have the same fitness, so just take
        // the fitness from the first phenotype
        double bestFitness = result.stream()
            .findFirst()
            .get()
            .fitness();

        Phenotype<BitGene, Double>[] phenotypes = result.stream()
            .toArray(Phenotype[]::new);

        List<List<OptimizableValue<?>>> paretoFront = new ArrayList<>();
        for (Phenotype<BitGene, Double> currentPheno : phenotypes) {
            List<SmodelBitChromosome> chromosomes = new ArrayList<>();
            for (int i = 0; i < currentPheno.genotype()
                .length(); i++) {
                SmodelBitChromosome currentChromosome = currentPheno.genotype()
                    .get(i)
                    .as(SmodelBitChromosome.class);
                chromosomes.add(currentChromosome);
            }
            paretoFront.add(normalizer.toOptimizableValues(chromosomes));
        }

        return new EAResult(bestFitness, paretoFront);
    }

}
