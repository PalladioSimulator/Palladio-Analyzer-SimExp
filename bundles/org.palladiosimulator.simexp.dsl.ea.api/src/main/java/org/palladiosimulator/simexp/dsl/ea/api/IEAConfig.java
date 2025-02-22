package org.palladiosimulator.simexp.dsl.ea.api;

import java.util.Optional;

public interface IEAConfig {

    Optional<Integer> populationSize();

    Optional<Integer> maxGenerations();

    Optional<Integer> steadyFitness();

    Optional<Double> mutationRate();

    Optional<Double> crossoverRate();
}
