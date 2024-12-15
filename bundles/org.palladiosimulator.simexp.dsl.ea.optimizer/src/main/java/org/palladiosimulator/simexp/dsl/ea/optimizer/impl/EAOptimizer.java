package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import static io.jenetics.engine.Limits.bySteadyFitness;

import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.IEAEvolutionStatusReceiver;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.IEAOptimizer;
import org.palladiosimulator.simexp.dsl.ea.api.IOptimizableProvider;

import io.jenetics.AnyChromosome;
import io.jenetics.AnyGene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.SinglePointCrossover;
import io.jenetics.SwapMutator;
import io.jenetics.TournamentSelector;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;

public class EAOptimizer implements IEAOptimizer {
    private final static Logger LOGGER = Logger.getLogger(EAOptimizer.class);

    @Override
    public void optimize(IOptimizableProvider optimizableProvider, IEAFitnessEvaluator fitnessEvaluator,
            IEAEvolutionStatusReceiver evolutionStatusReceiver) {
        LOGGER.info("EA running...");
        ////// to phenotype
        OptimizableRepresentationConverter converter = new OptimizableRepresentationConverter();
        List<CodecOptimizablePair> parsedCodecs = converter.parseOptimizables(optimizableProvider);

        OptimizableChromosomeFactory chromoCreator = new OptimizableChromosomeFactory();
        Codec<OptimizableChromosome, AnyGene<OptimizableChromosome>> codec = Codec.of(
                Genotype.of(AnyChromosome.of(chromoCreator.getNextChromosomeSupplier(parsedCodecs, fitnessEvaluator))),
                gt -> gt.gene()
                    .allele());
        ////// to phenotype end

        //// setup EA
        final Engine<AnyGene<OptimizableChromosome>, Double> engine = Engine.builder(chromoCreator::eval, codec)
            .populationSize(500)
            .constraint(new OptimizableChromosomeConstraint())
            .selector(new TournamentSelector<>(5))
            .offspringSelector(new TournamentSelector<>(5))
            .alterers(new SwapMutator<>(0.2), new SinglePointCrossover<>(0.1))
            .build();

        final EvolutionStatistics<Double, ?> statistics = EvolutionStatistics.ofNumber();

        final Phenotype<AnyGene<OptimizableChromosome>, Double> phenotype = engine.stream()
            .limit(bySteadyFitness(200))
            .limit(500)
            .peek(statistics)
            .collect(EvolutionResult.toBestPhenotype());

        LOGGER.info("EA finished...");

        evolutionStatusReceiver.reportStatus(converter.toPhenoValue(phenotype), phenotype.fitness());

        LOGGER.info(statistics);
    }

}
