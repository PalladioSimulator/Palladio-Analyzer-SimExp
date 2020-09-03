package org.palladiosimulator.simexp.environmentaldynamics.entity;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.ObservationImpl;

public class EnvironmentalStateObservation extends ObservationImpl<PerceivedValue<?>>
		implements PerceivableEnvironmentalState {

	private final EnvironmentalState hiddenState;

	private EnvironmentalStateObservation(PerceivedValue<?> value, EnvironmentalState hiddenState) {
		this.value = value;
		this.hiddenState = hiddenState;
	}

	public static EnvironmentalStateObservation of(PerceivedValue<?> value, EnvironmentalState hiddenState) {
		return new EnvironmentalStateObservation(value, hiddenState);
	}

	@Override
	public boolean isHidden() {
		return true;
	}

	@Override
	public PerceivedValue<?> getValue() {
		return value;
	}

	public EnvironmentalState getHiddenState() {
		return hiddenState;
	}

	@Override
	public boolean isInitial() {
		return hiddenState.isInitial();
	}

}
