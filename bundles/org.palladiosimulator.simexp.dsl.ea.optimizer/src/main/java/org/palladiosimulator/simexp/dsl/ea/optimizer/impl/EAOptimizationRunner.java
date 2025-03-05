package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import static io.jenetics.engine.Limits.bySteadyFitness;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.EAResult;
import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelIntegerChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.smodel.OptimizableIntegerChromoNormalizer;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

import io.jenetics.Genotype;
import io.jenetics.IntegerGene;
import io.jenetics.Phenotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionStream;
import io.jenetics.engine.Limits;
import io.jenetics.ext.moea.MOEA;
import io.jenetics.ext.moea.Vec;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

public class EAOptimizationRunner {

    private static final int DEFAULT_MAX_GENERATIONS = 100;
    private static final int DEFAULT_STEADY_FITNESS_GENERATION_NUMBER = 7;
    private final static Logger LOGGER = Logger.getLogger(EAOptimizer.class);

    @SuppressWarnings("unchecked")
    public EAResult runOptimization(IEAEvolutionStatusReceiver evolutionStatusReceiver,
            OptimizableIntegerChromoNormalizer normalizer, MOEAFitnessFunction fitnessFunction,
            final Engine<IntegerGene, Vec<double[]>> engine, IEAConfig config) {
        Genotype<IntegerGene> genotypeInstance = engine.genotypeFactory()
            .newInstance();
        ParetoCompatibleEvolutionStatistics paretoStatistics = new ParetoCompatibleEvolutionStatistics(fitnessFunction,
                genotypeInstance);

        EAReporter reporter = new EAReporter(evolutionStatusReceiver, normalizer);

        EvolutionStream<IntegerGene, Vec<double[]>> evolutionStream = engine.stream();
        if (config.steadyFitness()
            .isPresent()) {
            evolutionStream = evolutionStream.limit(bySteadyFitness(config.steadyFitness()
                .get()));
        }
        if (config.maxGenerations()
            .isPresent()) {
            evolutionStream = evolutionStream.limit(Limits.byFixedGeneration(config.maxGenerations()
                .get()));
        }
        ISeq<Phenotype<IntegerGene, Vec<double[]>>> result = evolutionStream.peek(reporter)
            .peek(paretoStatistics)
            .collect(MOEA.toParetoSet(IntRange.of(1, 10)));

        LOGGER.info("EA finished...");
        LOGGER.info(paretoStatistics);

        // all pareto efficient optimizables have the same fitness, so just take
        // the fitness from the first phenotype
        double bestFitness = result.stream()
            .findFirst()
            .get()
            .fitness()
            .data()[0];

        Phenotype<IntegerGene, Vec<double[]>>[] phenotypes = result.stream()
            .toArray(Phenotype[]::new);

        List<List<OptimizableValue<?>>> paretoFront = new ArrayList<>();
        for (Phenotype<IntegerGene, Vec<double[]>> currentPheno : phenotypes) {
            List<SmodelIntegerChromosome> chromosomes = new ArrayList<>();
            for (int i = 0; i < currentPheno.genotype()
                .length(); i++) {
                SmodelIntegerChromosome currentChromosome = currentPheno.genotype()
                    .get(i)
                    .as(SmodelIntegerChromosome.class);
                chromosomes.add(currentChromosome);
            }
            paretoFront.add(normalizer.toOptimizableValues(chromosomes));
        }

        return new EAResult(bestFitness, paretoFront);
    }

}
