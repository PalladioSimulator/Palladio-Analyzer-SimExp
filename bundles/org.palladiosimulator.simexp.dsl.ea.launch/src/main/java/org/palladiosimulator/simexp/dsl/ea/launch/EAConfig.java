package org.palladiosimulator.simexp.dsl.ea.launch;

import java.util.Optional;

import org.palladiosimulator.simexp.commons.constants.model.SimulationConstants;
import org.palladiosimulator.simexp.dsl.ea.api.IEAConfig;
import org.palladiosimulator.simexp.pcm.config.IEvolutionaryAlgorithmConfiguration;

import tools.mdsd.probdist.api.random.ISeedProvider;

public class EAConfig implements IEAConfig {
    private final double epsilon;
    private final Optional<ISeedProvider> seedProvider;
    private final IEvolutionaryAlgorithmConfiguration configuration;

    public EAConfig(double epsilon, Optional<ISeedProvider> seedProvider,
            IEvolutionaryAlgorithmConfiguration configuration) {
        this.epsilon = epsilon;
        this.seedProvider = seedProvider;
        this.configuration = configuration;
    }

    @Override
    public double getEpsilon() {
        return epsilon;
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
    public int survivorTournamentSize() {
        return SimulationConstants.DEFAULT_SURVIVOR_TOURNAMENT_SIZE;
    }

    @Override
    public int offspringTournamentSize() {
        return SimulationConstants.DEFAULT_OFFSPRING_TOURNAMENT_SIZE;
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
    public double mutationRate() {
        return configuration.getMutationRate();
    }

    @Override
    public double crossoverRate() {
        return configuration.getCrossoverRate();
    }

    @Override
    public double penaltyForInvalids() {
        return configuration.getErrorReward();
    }

}
