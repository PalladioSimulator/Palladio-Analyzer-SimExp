package org.palladiosimulator.simexp.dsl.ea.launch;

import java.util.Optional;

import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;
import org.palladiosimulator.simexp.pcm.config.IEvolutionaryAlgorithmConfiguration;

public class EAConfig implements IEAConfig {
    private final IEvolutionaryAlgorithmConfiguration configuration;

    public EAConfig(IEvolutionaryAlgorithmConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Optional<Integer> populationSize() {
        return Optional.of(configuration.getPopulationSize());
    }

    @Override
    public Optional<Integer> maxGenerations() {
        return configuration.getMaxGenerations();
    }

    @Override
    public Optional<Integer> steadyFitness() {
        return configuration.getSteadyFitness();
    }

    @Override
    public Optional<Double> mutationRate() {
        return configuration.getMutationRate();
    }

    @Override
    public Optional<Double> crossoverRate() {
        return configuration.getCrossoverRate();
    }

}
