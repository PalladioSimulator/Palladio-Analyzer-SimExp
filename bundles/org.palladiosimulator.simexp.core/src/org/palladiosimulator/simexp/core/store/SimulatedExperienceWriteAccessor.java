package org.palladiosimulator.simexp.core.store;

import java.util.List;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;

public interface SimulatedExperienceWriteAccessor extends BaseSimulatedExperienceAccessor {
    void setOptionalCache(SimulatedExperienceCache cache);

    void store(List<SimulatedExperience> trajectory);

    void store(SimulatedExperience simulatedExperience);

}
