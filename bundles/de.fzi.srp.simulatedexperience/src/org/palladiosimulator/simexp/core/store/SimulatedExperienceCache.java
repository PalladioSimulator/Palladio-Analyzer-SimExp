package org.palladiosimulator.simexp.core.store;

import java.util.Optional;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;

public interface SimulatedExperienceCache {
	
	public abstract Optional<SimulatedExperience> load(String id);
	
	public abstract void put(String id, SimulatedExperience simulatedExperience);
	
}
