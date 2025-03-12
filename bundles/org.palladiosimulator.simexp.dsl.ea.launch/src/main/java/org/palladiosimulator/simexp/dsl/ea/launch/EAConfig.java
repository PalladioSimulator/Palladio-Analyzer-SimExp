package org.palladiosimulator.simexp.dsl.ea.launch;

import java.util.Optional;

import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;
import org.palladiosimulator.simexp.pcm.config.IEvolutionaryAlgorithmConfiguration;

import tools.mdsd.probdist.api.random.ISeedProvider;

public class EAConfig implements IEAConfig {
    private final Optional<ISeedProvider> seedProvider;
    private final IEvolutionaryAlgorithmConfiguration configuration;

    public EAConfig(Optional<ISeedProvider> seedProvider, IEvolutionaryAlgorithmConfiguration configuration) {
        this.seedProvider = seedProvider;
        this.configuration = configuration;
    }

    @Override
    public Optional<ISeedProvider> getSeedProvider() {
        return seedProvider;
    }

    @Override
    public int populationSize() {
        return configuration.getPopulationSize();
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

    @Override
    public Optional<Double> penaltyForInvalids() {
        return Optional.empty();
    }

}
