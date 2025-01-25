package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.concurrent.Executor;

import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.constraints.OptimizableChromosomeBinaryConstraint;

import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.Mutator;
import io.jenetics.TournamentSelector;
import io.jenetics.UniformCrossover;
import io.jenetics.engine.Engine;

public class OptimizationEngineBuilder {

    public Engine<BitGene, Double> buildEngine(FitnessFunction fitnessFunction, Genotype<BitGene> genotype,
            int populationSize, Executor executor, int selectorSize, int offspringSelectorSize, double mutationRate,
            double crossoverRate) {

        return Engine.builder(fitnessFunction::apply, genotype)
            .populationSize(populationSize)
            .executor(executor)
            .constraint(new OptimizableChromosomeBinaryConstraint())
            .selector(new TournamentSelector<>(selectorSize))
            .offspringSelector(new TournamentSelector<>(offspringSelectorSize))
            .alterers(new Mutator<>(mutationRate), new UniformCrossover<>(crossoverRate))
            .build();
    }

}
