package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.concurrent.Executor;

import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.constraints.OptimizableChromosomeBinaryConstraint;

import io.jenetics.BitGene;
import io.jenetics.Crossover;
import io.jenetics.Genotype;
import io.jenetics.Mutator;
import io.jenetics.TournamentSelector;
import io.jenetics.UniformCrossover;
import io.jenetics.engine.Engine;
import io.jenetics.engine.Engine.Builder;
import io.jenetics.ext.moea.Vec;

public class EAOptimizationEngineBuilder {

    private static final int DEFAULT_POPULATION_SIZE = 80;

    private static final double DEFAULT_MUTATION_RATE = 0.01;

    private static final double DEFAULT_CROSSOVER_RATE = 0.8;

    private static final int SURVIVOR_SELECTOR_TOURNAMENT_SIZE = 5;

    private static final int OFFSPRING_SELECTOR_TOURNAMENT_SIZE = 5;

    private final double crossoverRate;

    private final double mutationRate;

    private IEAConfig config;

    public EAOptimizationEngineBuilder(IEAConfig config) {
        this.config = config;
        mutationRate = config.mutationRate()
            .orElse(DEFAULT_MUTATION_RATE);
        crossoverRate = config.crossoverRate()
            .orElse(DEFAULT_CROSSOVER_RATE);
    }

    public Engine<BitGene, Vec<double[]>> buildEngine(MOEAFitnessFunction fitnessFunction, Genotype<BitGene> genotype,
            Executor executor) {

        Builder<BitGene, Vec<double[]>> builder = Engine
            .builder(fitnessFunction::apply, new OptimizableChromosomeBinaryConstraint().constrain(genotype))
            .populationSize(config.populationSize())
            .executor(executor)
            .survivorsSelector(new TournamentSelector<>(SURVIVOR_SELECTOR_TOURNAMENT_SIZE))
            .offspringSelector(new TournamentSelector<>(OFFSPRING_SELECTOR_TOURNAMENT_SIZE));

        builder = addAlterers(builder);

        return builder.build();
    }

    private Builder<BitGene, Vec<double[]>> addAlterers(Builder<BitGene, Vec<double[]>> builder) {
        Mutator<BitGene, Vec<double[]>> mutator = new Mutator<>();
        Crossover<BitGene, Vec<double[]>> crossover = new UniformCrossover<>();
        if (config.mutationRate()
            .isPresent()) {
            mutator = new Mutator<>(config.mutationRate()
                .get());
        }
        if (config.crossoverRate()
            .isPresent()) {
            crossover = new UniformCrossover<>(config.crossoverRate()
                .get());
        }
        return builder.alterers(mutator, crossover);
    }

}
