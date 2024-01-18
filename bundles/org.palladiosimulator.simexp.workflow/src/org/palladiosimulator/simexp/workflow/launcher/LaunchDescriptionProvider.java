package org.palladiosimulator.simexp.workflow.launcher;

import org.palladiosimulator.simexp.core.store.DescriptionProvider;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStoreDescription;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.pcm.util.SimulationParameters;

class LaunchDescriptionProvider implements DescriptionProvider {
    private final String simulationID;
    private final int horizon;

    private SimulatedExperienceStoreDescription desc;

    public LaunchDescriptionProvider(SimulationParameters simulationParameters) {
        simulationID = simulationParameters.getSimulationID();
        horizon = simulationParameters.getNumberOfSimulationsPerRun();
    }

    public void setPolicyId(String policyId) {
        String sampleSpaceId = SimulatedExperienceConstants.constructSampleSpaceId(simulationID, policyId);
        desc = SimulatedExperienceStoreDescription.newBuilder()
            .withSimulationId(simulationID)
            .andSampleSpaceId(sampleSpaceId)
            .andSampleHorizon(horizon)
            .build();
    }

    @Override
    public SimulatedExperienceStoreDescription getDescription() {
        return desc;
    }
}
