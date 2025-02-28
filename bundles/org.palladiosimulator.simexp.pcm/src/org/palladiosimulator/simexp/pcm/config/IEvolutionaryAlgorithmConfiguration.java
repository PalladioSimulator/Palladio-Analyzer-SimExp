package org.palladiosimulator.simexp.pcm.config;

import java.util.Optional;

public interface IEvolutionaryAlgorithmConfiguration {
    int getPopulationSize();

    Optional<Integer> maxGenerations();

    Optional<Integer> steadyFitness();

    Optional<Double> mutationRate();

    Optional<Double> crossoverRate();
}
