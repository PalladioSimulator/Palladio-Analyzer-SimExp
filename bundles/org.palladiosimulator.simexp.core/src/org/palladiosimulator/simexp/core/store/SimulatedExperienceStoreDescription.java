package org.palladiosimulator.simexp.core.store;

public class SimulatedExperienceStoreDescription {

    private final int sampleHorizon;

    public SimulatedExperienceStoreDescription(int sampleHorizon) {
        this.sampleHorizon = sampleHorizon;
    }

    public int getSampleHorizon() {
        return sampleHorizon;
    }
}
