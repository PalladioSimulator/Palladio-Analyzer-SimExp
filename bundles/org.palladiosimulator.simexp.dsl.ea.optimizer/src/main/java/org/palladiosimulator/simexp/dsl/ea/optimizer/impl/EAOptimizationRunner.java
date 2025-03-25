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
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.smodel.OptimizableNormalizer;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

import io.jenetics.BitGene;
import io.jenetics.Genotype;
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
            OptimizableNormalizer normalizer, MOEAFitnessFunction fitnessFunction, final Engine<BitGene, Double> engine,
            IEAConfig config) {
        Genotype<BitGene> genotypeInstance = engine.genotypeFactory()
            .newInstance();
        ParetoCompatibleEvolutionStatistics paretoStatistics = new ParetoCompatibleEvolutionStatistics(fitnessFunction,
                genotypeInstance);

        EAReporter reporter = new EAReporter(evolutionStatusReceiver, normalizer);

        EvolutionStream<BitGene, Double> evolutionStream = addDerivedTerminationCriterion(engine, config);
        evolutionStream = addDirectTerminationCriterion(config, evolutionStream);

        Stream<EvolutionResult<BitGene, Double>> effectiveStream = evolutionStream.peek(reporter)
            .peek(paretoStatistics);

        final ISeq<Phenotype<BitGene, Double>> result;
        Collector<EvolutionResult<BitGene, Double>, ?, ISeq<Phenotype<BitGene, Double>>> paretoCollector = ParetoSetCollector
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

    private EvolutionStream<BitGene, Double> addDirectTerminationCriterion(IEAConfig config,
            EvolutionStream<BitGene, Double> evolutionStream) {
        if (config.maxGenerations()
            .isPresent()) {
            evolutionStream = evolutionStream.limit(Limits.byFixedGeneration(config.maxGenerations()
                .get()));
        }
        return evolutionStream;
    }

    private EvolutionStream<BitGene, Double> addDerivedTerminationCriterion(final Engine<BitGene, Double> engine,
            IEAConfig config) {
        EvolutionStream<BitGene, Double> evolutionStream = engine.stream();
        if (config.steadyFitness()
            .isPresent()) {
            evolutionStream = evolutionStream.limit(Limits.bySteadyFitness(config.steadyFitness()
                .get()));
        }
        return evolutionStream;
    }

}
