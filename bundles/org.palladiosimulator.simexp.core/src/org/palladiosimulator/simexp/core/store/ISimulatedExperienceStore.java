package org.palladiosimulator.simexp.core.store;

import java.util.Optional;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;

public interface ISimulatedExperienceStore {
    ISimulatedExperienceAccessor getAccessor();

    Optional<SimulatedExperience> findSelfAdaptiveSystemState(String id);
}
