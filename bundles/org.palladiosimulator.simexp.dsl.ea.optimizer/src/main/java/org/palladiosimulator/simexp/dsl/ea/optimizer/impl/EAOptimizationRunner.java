package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Stream;

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
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStream;
import io.jenetics.engine.Limits;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;
import tools.mdsd.probdist.api.random.ISeedProvider;

public class EAOptimizationRunner {

    private final static Logger LOGGER = Logger.getLogger(EAOptimizer.class);

    @SuppressWarnings("unchecked")
    public EAResult runOptimization(IEAEvolutionStatusReceiver evolutionStatusReceiver,
            OptimizableIntegerChromoNormalizer normalizer, MOEAFitnessFunction fitnessFunction,
            final Engine<IntegerGene, Double> engine, IEAConfig config) {
        Genotype<IntegerGene> genotypeInstance = engine.genotypeFactory()
            .newInstance();
        ParetoCompatibleEvolutionStatistics paretoStatistics = new ParetoCompatibleEvolutionStatistics(fitnessFunction,
                genotypeInstance);
        EAReporter reporter = new EAReporter(evolutionStatusReceiver, normalizer);

        EvolutionStream<IntegerGene, Double> evolutionStream = engine.stream();
        evolutionStream = addDerivedTerminationCriterion(engine, config);
        evolutionStream = addDirectTerminationCriterion(config, evolutionStream);
        Stream<EvolutionResult<IntegerGene, Double>> effectiveStream = evolutionStream.peek(reporter)
            .peek(paretoStatistics);

        final ISeq<Phenotype<IntegerGene, Double>> result;
        Collector<EvolutionResult<IntegerGene, Double>, ?, ISeq<Phenotype<IntegerGene, Double>>> paretoCollector = ParetoSetCollector
            .create();

        Optional<ISeedProvider> seedProvider = config.getSeedProvider();
        if (seedProvider.isEmpty()) {
            result = effectiveStream.collect(paretoCollector);
        } else {
            long seed = seedProvider.get()
                .getLong();
            result = RandomRegistry.with(new Random(seed), r -> effectiveStream.collect(paretoCollector));
        }

        LOGGER.info("EA finished...");
        LOGGER.info(paretoStatistics);

        return createEAResult(normalizer, result);
    }

    private EAResult createEAResult(OptimizableIntegerChromoNormalizer normalizer,
            ISeq<Phenotype<IntegerGene, Double>> result) {
        // all pareto efficient optimizables have the same fitness, so just take
        // the fitness from the first phenotype
        double bestFitness = result.stream()
            .findFirst()
            .get()
            .fitness();

        Phenotype<IntegerGene, Double>[] phenotypes = result.stream()
            .toArray(Phenotype[]::new);

        List<List<OptimizableValue<?>>> paretoFront = new ArrayList<>();
        for (Phenotype<IntegerGene, Double> currentPheno : phenotypes) {
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

    private EvolutionStream<IntegerGene, Double> addDirectTerminationCriterion(IEAConfig config,
            EvolutionStream<IntegerGene, Double> evolutionStream) {
        if (config.maxGenerations()
            .isPresent()) {
            evolutionStream = evolutionStream.limit(Limits.byFixedGeneration(config.maxGenerations()
                .get()));
        }
        return evolutionStream;
    }

    private EvolutionStream<IntegerGene, Double> addDerivedTerminationCriterion(
            final Engine<IntegerGene, Double> engine, IEAConfig config) {
        EvolutionStream<IntegerGene, Double> evolutionStream = engine.stream();
        if (config.steadyFitness()
            .isPresent()) {
            evolutionStream = evolutionStream.limit(Limits.bySteadyFitness(config.steadyFitness()
                    .get()));
        }
        return evolutionStream;
    }

}
