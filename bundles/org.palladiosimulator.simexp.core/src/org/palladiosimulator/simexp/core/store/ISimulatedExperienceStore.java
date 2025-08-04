package org.palladiosimulator.simexp.core.store;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;

public interface ISimulatedExperienceStore {
    Iterator<List<SimulatedExperience>> iterator();

    Optional<SimulatedExperience> findSelfAdaptiveSystemState(String id);

    Optional<List<SimulatedExperience>> getTrajectoryAt(int index);
}
