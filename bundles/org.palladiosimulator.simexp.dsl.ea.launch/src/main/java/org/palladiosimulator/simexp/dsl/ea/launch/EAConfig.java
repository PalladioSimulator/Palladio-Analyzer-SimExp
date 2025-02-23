package org.palladiosimulator.simexp.dsl.ea.launch;

import java.util.Optional;

import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;

public class EAConfig implements IEAConfig {

    @Override
    public Optional<Integer> populationSize() {
        return Optional.of(80);
    }

    @Override
    public Optional<Integer> maxGenerations() {
        return Optional.of(50);
    }

    @Override
    public Optional<Integer> steadyFitness() {
        return Optional.of(12);
    }

    @Override
    public Optional<Double> mutationRate() {
        return Optional.of(0.01);
    }

    @Override
    public Optional<Double> crossoverRate() {
        return Optional.of(0.8);
    }

}
