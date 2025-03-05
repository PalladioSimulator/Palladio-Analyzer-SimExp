package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.concurrent.Executor;

import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;

import io.jenetics.Crossover;
import io.jenetics.Genotype;
import io.jenetics.IntegerGene;
import io.jenetics.Mutator;
import io.jenetics.TournamentSelector;
import io.jenetics.UniformCrossover;
import io.jenetics.engine.Engine;
import io.jenetics.engine.Engine.Builder;
import io.jenetics.ext.moea.Vec;

public class EAOptimizationEngineBuilder {

    private static final int DEFAULT_POPULATION_SIZE = 80;

    private static final double DEFAULT_MUTATION_RATE = 0.0125;

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

    public Engine<IntegerGene, Vec<double[]>> buildEngine(MOEAFitnessFunction fitnessFunction,
            Genotype<IntegerGene> genotype, Executor executor) {

        Builder<IntegerGene, Vec<double[]>> builder = Engine.builder(fitnessFunction::apply, genotype)
            .populationSize(config.populationSize())
            .executor(executor)
            .survivorsSelector(new TournamentSelector<>(SURVIVOR_SELECTOR_TOURNAMENT_SIZE))
            .offspringSelector(new TournamentSelector<>(OFFSPRING_SELECTOR_TOURNAMENT_SIZE));

        builder = addAlterers(builder);

        return builder.build();
    }

    private Builder<IntegerGene, Vec<double[]>> addAlterers(Builder<IntegerGene, Vec<double[]>> builder) {
        Mutator<IntegerGene, Vec<double[]>> mutator = new Mutator<>();
        Crossover<IntegerGene, Vec<double[]>> crossover = new UniformCrossover<>();
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
