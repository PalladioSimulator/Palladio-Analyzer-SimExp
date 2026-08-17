package org.palladiosimulator.simexp.pcm.config;

import java.util.Optional;

public interface IEvolutionaryAlgorithmConfiguration {
    int getPopulationSize();

    double getErrorReward();

    double getMutationRate();

    double getCrossoverRate();

    Optional<Integer> getMaxGenerations();

    Optional<Integer> getSteadyFitness();

    int getMemoryUsage();
}
