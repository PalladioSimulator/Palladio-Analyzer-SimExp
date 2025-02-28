package org.palladiosimulator.simexp.dsl.ea.optimizer.utility;

import java.util.Optional;

import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;

public class ConfigHelper implements IEAConfig {

    private int populationSize = 80;
    private Optional<Double> mutationRate;
    private Optional<Double> crossoverRate;
    private Optional<Integer> steadyFitness;
    private Optional<Integer> maxGenerations;

    public ConfigHelper() {
        mutationRate = Optional.empty();
        crossoverRate = Optional.empty();
    };

    public ConfigHelper(int populationSize, double mutationRate, double crossoverRate, int steadyFitness,
            int maxGenerations) {
        this.populationSize = populationSize;
        this.steadyFitness = Optional.of(steadyFitness);
        this.maxGenerations = Optional.of(maxGenerations);
        this.mutationRate = Optional.of(mutationRate);
        this.crossoverRate = Optional.of(crossoverRate);

    }

    @Override
    public int populationSize() {
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
