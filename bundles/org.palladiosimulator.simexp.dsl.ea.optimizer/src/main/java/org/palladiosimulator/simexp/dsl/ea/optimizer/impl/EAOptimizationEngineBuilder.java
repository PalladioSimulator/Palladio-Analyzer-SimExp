package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.concurrent.Executor;

import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.constraints.OptimizableChromosomeBinaryConstraint;

import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.Mutator;
import io.jenetics.TournamentSelector;
import io.jenetics.UniformCrossover;
import io.jenetics.engine.Engine;
import io.jenetics.ext.moea.Vec;

public class EAOptimizationEngineBuilder {

    private static final int DEFAULT_POPULATION_SIZE = 80;

    private static final double DEFAULT_MUTATION_RATE = 0.01;

    private static final double DEFAULT_CROSSOVER_RATE = 0.8;

    private static final int SURVIVOR_SELECTOR_TOURNAMENT_SIZE = 5;

    private static final int OFFSPRING_SELECTOR_TOURNAMENT_SIZE = 5;

    private final int populationSize;

    private final double crossoverRate;

    private final double mutationRate;

    public EAOptimizationEngineBuilder(IEAConfig config) {
        mutationRate = config.mutationRate()
            .orElse(DEFAULT_MUTATION_RATE);
        crossoverRate = config.crossoverRate()
            .orElse(DEFAULT_CROSSOVER_RATE);
        populationSize = config.populationSize()
            .orElse(DEFAULT_POPULATION_SIZE);
    }

    public Engine<BitGene, Vec<double[]>> buildEngine(MOEAFitnessFunction fitnessFunction, Genotype<BitGene> genotype,
            Executor executor) {

        return Engine.builder(fitnessFunction::apply, new OptimizableChromosomeBinaryConstraint().constrain(genotype))
            .populationSize(populationSize)
            .executor(executor)
            .survivorsSelector(new TournamentSelector<>(SURVIVOR_SELECTOR_TOURNAMENT_SIZE))
            .offspringSelector(new TournamentSelector<>(OFFSPRING_SELECTOR_TOURNAMENT_SIZE))
            .alterers(new Mutator<>(mutationRate), new UniformCrossover<>(crossoverRate))
            .build();
    }

}
