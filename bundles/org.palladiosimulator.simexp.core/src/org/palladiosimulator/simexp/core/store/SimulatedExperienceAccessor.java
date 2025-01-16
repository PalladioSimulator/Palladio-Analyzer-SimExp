package org.palladiosimulator.simexp.core.store;

public interface SimulatedExperienceAccessor {
    SimulatedExperienceWriteAccessor createSimulatedExperienceWriteAccessor();

    SimulatedExperienceReadAccessor createSimulatedExperienceReadAccessor();

}
