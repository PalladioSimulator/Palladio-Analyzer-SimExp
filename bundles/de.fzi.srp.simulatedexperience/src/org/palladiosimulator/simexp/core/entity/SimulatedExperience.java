package org.palladiosimulator.simexp.core.entity;

public interface SimulatedExperience {

	public String getConfigurationDifferenceBefore();
	
	public String getConfigurationDifferenceAfter();
	
	public String getReconfiguration();
	
	public String getQuantifiedStateOfCurrent();
	
	public String getQuantifiedStateOfNext();
	
	public String getEnvironmentalStateBefore();
	
	public String getEnvironmentalStateAfter();
	
	public String getEnvironmentalStateObservation();
	
	public String getReward();
	
	public String getId();
	
}
