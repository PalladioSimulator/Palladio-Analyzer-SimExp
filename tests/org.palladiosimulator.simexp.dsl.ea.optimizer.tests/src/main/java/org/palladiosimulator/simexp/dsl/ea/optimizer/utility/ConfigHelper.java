package org.palladiosimulator.simexp.dsl.ea.optimizer.utility;

import java.util.Optional;

import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;

public class ConfigHelper implements IEAConfig {

    private Optional<Integer> populationSize;
    private Optional<Double> mutationRate;
    private Optional<Double> crossoverRate;

    public ConfigHelper() {
        populationSize = Optional.empty();
        mutationRate = Optional.empty();
        crossoverRate = Optional.empty();
    };

    public ConfigHelper(int populationSize, double mutationRate, double crossoverRate) {
        this.populationSize = Optional.of(populationSize);
        this.mutationRate = Optional.of(mutationRate);
        this.crossoverRate = Optional.of(crossoverRate);

    }

    @Override
    public Optional<Integer> populationSize() {
        return populationSize;
    }

    @Override
    public Optional<Integer> maxGenerations() {
        return Optional.empty();
    }

    @Override
    public Optional<Integer> steadyFitness() {
        // TODO Auto-generated method stub
        return Optional.empty();
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
