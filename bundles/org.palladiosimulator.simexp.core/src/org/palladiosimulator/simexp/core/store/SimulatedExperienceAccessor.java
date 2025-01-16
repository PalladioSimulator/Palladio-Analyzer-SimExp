package org.palladiosimulator.simexp.core.store;

public interface SimulatedExperienceAccessor {
    void setOptionalCache(SimulatedExperienceCache guavaSimulatedExperienceCache);

    SimulatedExperienceWriteAccessor createSimulatedExperienceWriteAccessor();

    SimulatedExperienceReadAccessor createSimulatedExperienceReadAccessor();

}
