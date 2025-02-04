package org.palladiosimulator.simexp.dsl.ea.optimizer.impl;

import java.util.concurrent.Executor;

import org.palladiosimulator.simexp.dsl.ea.optimizer.operators.OHCrossover;
import org.palladiosimulator.simexp.dsl.ea.optimizer.operators.OHMutator;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.BitInterpreter;

import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.TournamentSelector;
import io.jenetics.engine.Constraint;
import io.jenetics.engine.Engine;
import io.jenetics.ext.moea.Vec;

public class EAOptimizationEngineBuilder {

    public Engine<BitGene, Vec<double[]>> buildEngine(MOEAFitnessFunction fitnessFunction, Genotype<BitGene> genotype,
            BitInterpreter bitInterpreter, int populationSize, Executor executor, int selectorSize,
            int offspringSelectorSize, double mutationRate, double crossoverRate) {

//        return Engine.builder(fitnessFunction::apply, genotype)
//            .populationSize(populationSize)
//            .executor(executor)
//            .selector(new TournamentSelector<>(selectorSize))
//            .offspringSelector(new TournamentSelector<>(offspringSelectorSize))
//            .alterers(new Mutator<>(mutationRate), new UniformCrossover<>(crossoverRate))
//            .build();
        Constraint<BitGene, Vec<double[]>> constraint = bitInterpreter.getCorrespondingConstraint();

        return Engine.builder(fitnessFunction::apply, constraint.constrain(genotype))
            .populationSize(populationSize)
            .executor(executor)
            .constraint(constraint)
            .selector(new TournamentSelector<>(selectorSize))
            .offspringSelector(new TournamentSelector<>(offspringSelectorSize))
            .alterers(new OHMutator(mutationRate), new OHCrossover(crossoverRate, 0.8))
            .build();
    }

}
