package org.palladiosimulator.simexp.core.store;

import java.util.List;
import java.util.Optional;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;

public interface SimulatedExperienceAccessor {
	
	public void setOptionalCache(SimulatedExperienceCache cache);
	
	public void connect(SimulatedExperienceStoreDescription desc);
	
	public void close();
	
	public void store(List<SimulatedExperience> trajectory);
	
	public void store(SimulatedExperience simulatedExperience);
	
	public Optional<SimulatedExperience> findSimulatedExperience(String id);
	
	public Optional<SimulatedExperience> findSelfAdaptiveSystemState(String id);
	
	public Optional<List<SimulatedExperience>> getTrajectoryAt(int index);
	
}
