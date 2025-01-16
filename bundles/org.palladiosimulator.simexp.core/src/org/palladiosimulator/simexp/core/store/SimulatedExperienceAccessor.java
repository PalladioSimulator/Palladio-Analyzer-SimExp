package org.palladiosimulator.simexp.core.store;

public interface SimulatedExperienceAccessor extends SimulatedExperienceWriteAccessor, SimulatedExperienceReadAccessor {

    void setOptionalCache(SimulatedExperienceCache cache);

    void connect(SimulatedExperienceStoreDescription desc);

    void close();
}
