package org.palladiosimulator.simexp.core.store;

public interface ISimulatedExperienceAccessor {
    SimulatedExperienceWriteAccessor createSimulatedExperienceWriteAccessor(SimulatedExperienceStoreDescription desc);

    SimulatedExperienceReadAccessor createSimulatedExperienceReadAccessor();

}
