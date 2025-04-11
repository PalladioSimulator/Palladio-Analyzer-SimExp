package org.palladiosimulator.simexp.commons.constants.model;

public interface SimulationConstants {
    static final String SIMULATION_ID = "SimulationID";
    static final String NUMBER_OF_RUNS = "NumberOfRuns";
    static final String NUMBER_OF_SIMULATIONS_PER_RUN = "NumberOfSimulationsPerRun";

    static final int DEFAULT_NUMBER_OF_RUNS = 1;
    static final int DEFAULT_NUMBER_OF_SIMULATIONS_PER_RUN = 100;

    static final String CUSTOM_SEED = "CustomSeed";

    static final String POPULATION_SIZE = "PopulationSize";
    static final String MAX_GENERATIONS = "MaxGenerations";
    static final String STEADY_FITNESS = "SteadyFitness";
    static final String MUTATION_RATE = "MutationRate";
    static final String CROSSOVER_RATE = "CrossoverRate";

    static final int DEFAULT_POPULATION_SIZE = 100;

    static final String SIMULATOR_TYPE = "Simulator Type";
    static final String SIMULATION_ENGINE = "Simulation Engine";
    static final String QUALITY_OBJECTIVE = "Quality Objective";
    static final String TRANSFORMATIONS_ACTIVE = "TransformationsActive";
    static final String REWARD_TYPE = "Reward Type";

    static final String MODELLED_OPTIMIZATION_TYPE = "ModelledOptimizationType";

    static final SimulatorType DEFAULT_SIMULATOR_TYPE = SimulatorType.MODELLED;
    static final RewardType DEFAULT_REWARD_TYPE = RewardType.EXPECTED;
    static final SimulationEngine DEFAULT_SIMULATION_ENGINE = SimulationEngine.PCM;
    static final QualityObjective DEFAULT_QUALITY_OBJECTIVE = QualityObjective.PERFORMANCE;

    static final ModelledOptimizationType DEFAULT_MODELLED_OPTIMIZATION_TYPE = ModelledOptimizationType.SIMPLE;

    static final String OPTIMIZED_VALUES = "optimizedValues";
}
