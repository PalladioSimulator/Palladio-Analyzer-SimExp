package org.palladiosimulator.simexp.core.store;

import java.util.List;
import java.util.Optional;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;

public interface SimulatedExperienceReadAccessor extends BaseSimulatedExperienceAccessor {
    void connect(SimulatedExperienceStoreDescription desc);

    void close();

    Optional<SimulatedExperience> findSimulatedExperience(String id);

    Optional<SimulatedExperience> findSelfAdaptiveSystemState(String id);

    Optional<List<SimulatedExperience>> getTrajectoryAt(int index);
}
