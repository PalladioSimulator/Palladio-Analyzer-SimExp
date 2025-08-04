package org.palladiosimulator.simexp.core.store;

public class SimulatedExperienceStoreDescription {

    private final String simulationId;
    private final String sampleSpaceId;
    private final int sampleHorizon;

    public SimulatedExperienceStoreDescription(String simulationId, String sampleSpaceId, int sampleHorizon) {
        this.simulationId = simulationId;
        this.sampleSpaceId = sampleSpaceId;
        this.sampleHorizon = sampleHorizon;
    }

    public int getSampleHorizon() {
        return sampleHorizon;
    }

    public String getSampleSpaceId() {
        return sampleSpaceId;
    }

    public String getSimulationId() {
        return simulationId;
    }
}
