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

    public EvolutionaryAlgorithmConfiguration(int eaPopulationSize, double errorDefault,
            Optional<Integer> maxGenerations, Optional<Integer> steadyFitness, Optional<Double> mutationRate,
            Optional<Double> crossoverRate) {
        this.populationSize = eaPopulationSize;
        this.errorDefault = errorDefault;
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
    public double getErrorDefault() {
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
}
