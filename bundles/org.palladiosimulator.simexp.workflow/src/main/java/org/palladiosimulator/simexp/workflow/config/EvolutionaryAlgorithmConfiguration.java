package org.palladiosimulator.simexp.workflow.config;

import java.util.Optional;

import org.palladiosimulator.simexp.pcm.config.IEvolutionaryAlgorithmConfiguration;

public class EvolutionaryAlgorithmConfiguration implements IEvolutionaryAlgorithmConfiguration {
    private final int populationSize;
    private final Optional<Integer> maxGenerations;
    private final Optional<Integer> steadyFitness;
    private final Optional<Double> mutationRate;
    private final Optional<Double> crossoverRate;

    public EvolutionaryAlgorithmConfiguration(int eaPopulationSize, Optional<Integer> maxGenerations,
            Optional<Integer> steadyFitness, Optional<Double> mutationRate, Optional<Double> crossoverRate) {
        this.populationSize = eaPopulationSize;
        this.maxGenerations = maxGenerations;
        this.steadyFitness = steadyFitness;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
    }

    @Override
    public int getPopulationSize() {
        return populationSize;
    }

    @Override
    public Optional<Integer> maxGenerations() {
        return maxGenerations;
    }

    @Override
    public Optional<Integer> steadyFitness() {
        return steadyFitness;
    }

    @Override
    public Optional<Double> mutationRate() {
        return mutationRate;
    }

    @Override
    public Optional<Double> crossoverRate() {
        return crossoverRate;
    }
}
