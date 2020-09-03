package org.palladiosimulator.simexp.environmentaldynamics.entity;

public interface PerceivableEnvironmentalState {

	public boolean isHidden();
	
	public boolean isInitial();
	
	public PerceivedValue<?> getValue();
	
	default public String getStringRepresentation() {
		return getValue().toString();
	}
	
}
