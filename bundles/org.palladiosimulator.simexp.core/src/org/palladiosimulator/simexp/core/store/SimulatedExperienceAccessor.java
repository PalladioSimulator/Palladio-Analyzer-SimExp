package org.palladiosimulator.simexp.core.store;

public interface SimulatedExperienceAccessor extends SimulatedExperienceReadAccessor {
    SimulatedExperienceWriteAccessor createSimulatedExperienceWriteAccessor();

}
