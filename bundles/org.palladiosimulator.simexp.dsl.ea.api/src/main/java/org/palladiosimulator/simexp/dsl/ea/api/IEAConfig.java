package org.palladiosimulator.simexp.dsl.ea.api;

import java.util.Optional;

public interface IEAConfig {
    int populationSize();

    Optional<Integer> maxGenerations();

    Optional<Integer> steadyFitness();
}
