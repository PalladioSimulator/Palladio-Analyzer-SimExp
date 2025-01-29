package org.palladiosimulator.simexp.dsl.ea.launch;

import java.util.Optional;

import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;

public class EAConfig implements IEAConfig {

    @Override
    public int populationSize() {
        return 100;
    }

    @Override
    public Optional<Integer> maxGenerations() {
        return Optional.empty();
    }

    @Override
    public Optional<Integer> steadyFitness() {
        return Optional.empty();
    }

}
