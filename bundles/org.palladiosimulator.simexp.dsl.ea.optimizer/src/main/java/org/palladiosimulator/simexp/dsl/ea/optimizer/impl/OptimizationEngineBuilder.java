package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.concurrent.Executor;

import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.Mutator;
import io.jenetics.TournamentSelector;
import io.jenetics.UniformCrossover;
import io.jenetics.engine.Engine;
import io.jenetics.ext.moea.Vec;

public class OptimizationEngineBuilder {

    public Engine<BitGene, Vec<double[]>> buildEngine(MOEAFitnessFunction fitnessFunction, Genotype<BitGene> genotype,
            int populationSize, Executor executor, int selectorSize, int offspringSelectorSize, double mutationRate,
            double crossoverRate) {

        return Engine.builder(fitnessFunction::apply, genotype)
            .populationSize(populationSize)
            .executor(executor)
            .selector(new TournamentSelector<>(selectorSize))
            .offspringSelector(new TournamentSelector<>(offspringSelectorSize))
            .alterers(new Mutator<>(mutationRate), new UniformCrossover<>(crossoverRate))
            .build();

//        return Engine.builder(fitnessFunction::apply, genotype)
//            .populationSize(populationSize)
//            .executor(executor)
//            .constraint(new OptimizableChromosomeBinaryConstraint())
//            .selector(new TournamentSelector<>(selectorSize))
//            .offspringSelector(new TournamentSelector<>(offspringSelectorSize))
//            .alterers(new Mutator<>(mutationRate), new UniformCrossover<>(crossoverRate))
//            .build();
    }

}
