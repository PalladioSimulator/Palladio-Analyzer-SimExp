package org.palladiosimulator.simexp.pcm.config;

public class SimulationParameters {
    private final String simulationID;
    private final int numberOfRuns;
    private final int numberOfSimulationsPerRun;

    public SimulationParameters(String simulationId, int numberOfRuns, int numberOfSimulationsPerRun) {
        this.simulationID = simulationId;
        this.numberOfRuns = numberOfRuns;
        this.numberOfSimulationsPerRun = numberOfSimulationsPerRun;
    }

    public String getSimulationID() {
        return simulationID;
    }

    public int getNumberOfRuns() {
        return numberOfRuns;
    }

    public int getNumberOfSimulationsPerRun() {
        return numberOfSimulationsPerRun;
    }
}