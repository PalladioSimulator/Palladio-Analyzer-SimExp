package org.palladiosimulator.simexp.workflow.config;

import java.util.Optional;

import org.palladiosimulator.simexp.pcm.config.IEvolutionaryAlgorithmConfiguration;

public class EvolutionaryAlgorithmConfiguration implements IEvolutionaryAlgorithmConfiguration {
    private final int populationSize;
    private final double errorDefault;
    private final Optional<Integer> maxGenerations;
    private final Optional<Integer> steadyFitness;
    private final Optional<Double> mutationRate;
    private final Optional<Double> crossoverRate;
    private final int memoryUsage;

    public EvolutionaryAlgorithmConfiguration(int eaPopulationSize, double errorDefault,
            Optional<Integer> maxGenerations, Optional<Integer> steadyFitness, Optional<Double> mutationRate,
            Optional<Double> crossoverRate, int memoryUsage) {
        this.populationSize = eaPopulationSize;
        this.errorDefault = errorDefault;
        this.maxGenerations = maxGenerations;
        this.steadyFitness = steadyFitness;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.memoryUsage = memoryUsage;
    }

    @Override
    public int getPopulationSize() {
        return populationSize;
    }

    @Override
    public double getErrorReward() {
        return errorDefault;
    }

    @Override
    public Optional<Integer> getMaxGenerations() {
        return maxGenerations;
    }

    @Override
    public Optional<Integer> getSteadyFitness() {
        return steadyFitness;
    }

    @Override
    public Optional<Double> getMutationRate() {
        return mutationRate;
    }

    @Override
    public Optional<Double> getCrossoverRate() {
        return crossoverRate;
    }

    @Override
    public int getMemoryUsage() {
        return memoryUsage;
    }
}
