package org.palladiosimulator.simexp.pcm.config;

import java.util.Optional;

public interface IEvolutionaryAlgorithmConfiguration {
    int getPopulationSize();

    double getErrorDefault();

    Optional<Integer> getMaxGenerations();

    Optional<Integer> getSteadyFitness();

    Optional<Double> getMutationRate();

    Optional<Double> getCrossoverRate();
}
