package org.palladiosimulator.simexp.pcm.util;

public class SimulationParameters {
	private String simulationID;
	private int numberOfRuns;
	private int numberOfSimulationsPerRun;
	
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