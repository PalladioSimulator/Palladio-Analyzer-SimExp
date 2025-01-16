package org.palladiosimulator.simexp.core.store;

import java.util.List;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;

public interface SimulatedExperienceWriteAccessor {
    void setOptionalCache(SimulatedExperienceCache cache);

    void store(SimulatedExperienceStoreDescription desc, List<SimulatedExperience> trajectory);

    void store(SimulatedExperienceStoreDescription desc, SimulatedExperience simulatedExperience);

}
