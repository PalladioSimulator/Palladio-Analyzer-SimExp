package org.palladiosimulator.simexp.core.store;

import java.util.List;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;

public interface SimulatedExperienceWriteAccessor extends BaseSimulatedExperienceAccessor {
    void connect(SimulatedExperienceStoreDescription desc);

    void store(List<SimulatedExperience> trajectory);

    void store(SimulatedExperience simulatedExperience);

}
