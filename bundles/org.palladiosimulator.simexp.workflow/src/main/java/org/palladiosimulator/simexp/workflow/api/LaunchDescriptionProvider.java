package org.palladiosimulator.simexp.workflow.api;

import org.palladiosimulator.simexp.core.store.DescriptionProvider;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStoreDescription;
import org.palladiosimulator.simexp.pcm.config.SimulationParameters;

public class LaunchDescriptionProvider implements DescriptionProvider {
    private final int horizon;

    private SimulatedExperienceStoreDescription desc;

    public LaunchDescriptionProvider(SimulationParameters simulationParameters) {
        horizon = simulationParameters.getNumberOfSimulationsPerRun();
    }

    public void setPolicyId(String policyId) {
        desc = new SimulatedExperienceStoreDescription(horizon);
    }

    @Override
    public SimulatedExperienceStoreDescription getDescription() {
        return desc;
    }
}
