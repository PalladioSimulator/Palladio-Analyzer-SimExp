package org.palladiosimulator.simexp.commons.constants.model;

public interface SimulationConstants {

    public static final String EMPTY_STRING = "";
    public static final int DEFAULT_NUMBER_OF_RUNS = 1;
    public static final int DEFAULT_NUMBER_OF_SIMULATIONS_PER_RUN = 100;

    public static final String SIMULATION_ID = "SimulationID";
    public static final String NUMBER_OF_RUNS = "NumberOfRuns";
    public static final String NUMBER_OF_SIMULATIONS_PER_RUN = "NumberOfSimulationsPerRun";

    public static final String CUSTOM_SEED = "CustomSeed";

    public static final String SIMULATOR_TYPE = "Simulator Type";
    public static final String SIMULATION_ENGINE = "Simulation Engine";
    public static final String QUALITY_OBJECTIVE = "Quality Objective";
    public static final String TRANSFORMATIONS_ACTIVE = "TransformationsActive";

    public static final SimulatorType DEFAULT_SIMULATOR_TYPE = SimulatorType.MODELLED;
    public static final SimulationEngine DEFAULT_SIMULATION_ENGINE = SimulationEngine.PCM;
    public static final QualityObjective DEFAULT_QUALITY_OBJECTIVE = QualityObjective.PERFORMANCE;

}
