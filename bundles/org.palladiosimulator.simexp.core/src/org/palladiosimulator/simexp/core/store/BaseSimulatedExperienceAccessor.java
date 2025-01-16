package org.palladiosimulator.simexp.core.store;

public interface BaseSimulatedExperienceAccessor {
    void connect(SimulatedExperienceStoreDescription desc);

    void close();
}
