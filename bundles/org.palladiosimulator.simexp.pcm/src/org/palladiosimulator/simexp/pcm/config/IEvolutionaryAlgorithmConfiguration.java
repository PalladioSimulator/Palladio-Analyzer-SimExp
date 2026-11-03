package org.palladiosimulator.simexp.pcm.config;

import java.util.Optional;

public interface IEvolutionaryAlgorithmConfiguration extends IKubernetesConfiguration {
    int getPopulationSize();

    double getMutationRate();

    double getCrossoverRate();

    Optional<Integer> getMaxGenerations();

    Optional<Integer> getSteadyFitness();
}
