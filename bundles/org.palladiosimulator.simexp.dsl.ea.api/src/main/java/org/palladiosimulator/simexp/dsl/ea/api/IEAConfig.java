package org.palladiosimulator.simexp.dsl.ea.api;

import java.util.Optional;

import tools.mdsd.probdist.api.random.ISeedProvider;

public interface IEAConfig {
    Optional<ISeedProvider> getSeedProvider();

    int populationSize();

    Optional<Integer> maxGenerations();

    Optional<Integer> steadyFitness();

    Optional<Double> mutationRate();

    Optional<Double> crossoverRate();

    double penaltyForInvalids();
}
