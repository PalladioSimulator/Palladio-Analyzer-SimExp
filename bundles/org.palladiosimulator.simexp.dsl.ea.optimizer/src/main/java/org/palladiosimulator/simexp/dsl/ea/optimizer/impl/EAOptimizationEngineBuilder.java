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

public class EAOptimizationEngineBuilder {

    private static final int SURVIVOR_SELECTOR_TOURNAMENT_SIZE = 5;

    private static final int OFFSPRING_SELECTOR_TOURNAMENT_SIZE = 5;

    private IEAConfig config;

    public EAOptimizationEngineBuilder(IEAConfig config) {
        this.config = config;
    }

    public Engine<BitGene, Double> buildEngine(EAFitnessFunction fitnessFunction, Genotype<BitGene> genotype,
            Executor executor) {

        Builder<BitGene, Double> builder = Engine
            .builder(fitnessFunction::apply, new OptimizableChromosomeBinaryConstraint().constrain(genotype))
            .populationSize(config.populationSize())
            .executor(executor)
            .survivorsSelector(new TournamentSelector<>(SURVIVOR_SELECTOR_TOURNAMENT_SIZE))
            .offspringSelector(new TournamentSelector<>(OFFSPRING_SELECTOR_TOURNAMENT_SIZE));

        builder = addAlterers(builder);

        return builder.build();
    }

    private Builder<BitGene, Double> addAlterers(Builder<BitGene, Double> builder) {
        Mutator<BitGene, Double> mutator = new Mutator<>();
        Crossover<BitGene, Double> crossover = new UniformCrossover<>();
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
