package org.palladiosimulator.simexp.environmentaldynamics.entity;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.ObservationImpl;

public class EnvironmentalStateObservation extends ObservationImpl<PerceivedValue<?>> implements PerceivableEnvironmentalState {
	
	private final HiddenEnvironmentalState source;
	
	private EnvironmentalStateObservation(PerceivedValue<?> value, HiddenEnvironmentalState source) {
		this.value = value;
		this.source = source;
	}
	
	public static EnvironmentalStateObservation of(PerceivedValue<?> value, HiddenEnvironmentalState source) {
		return new EnvironmentalStateObservation(value, source);
	}

	@Override
	public boolean isHidden() {
		return true;
	}

	@Override
	public PerceivedValue<?> getValue() {
		return value;
	}
	
	public HiddenEnvironmentalState getHiddenSourceState() {
		return source;
	}

}
