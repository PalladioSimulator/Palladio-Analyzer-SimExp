package org.palladiosimulator.simexp.dsl.ea.launch;

import java.util.Optional;

import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;

public class EAConfig implements IEAConfig {

    @Override
    public int populationSize() {
        return 40;
    }

    @Override
    public Optional<Integer> maxGenerations() {
        return Optional.of(25);
    }

    @Override
    public Optional<Integer> steadyFitness() {
        return Optional.of(12);
    }

    @Override
    public Optional<Double> mutationRate() {
        return Optional.of(0.025);
    }

    @Override
    public Optional<Double> crossoverRate() {
        return Optional.of(0.8);
    }

}
