package org.palladiosimulator.simexp.dsl.ea.api;

import java.util.Optional;

import tools.mdsd.probdist.api.random.ISeedProvider;

public interface IEAConfig {
    Optional<ISeedProvider> getSeedProvider();

    int populationSize();

    double mutationRate();

    double crossoverRate();

    Optional<Integer> maxGenerations();

    Optional<Integer> steadyFitness();

    double penaltyForInvalids();
}
