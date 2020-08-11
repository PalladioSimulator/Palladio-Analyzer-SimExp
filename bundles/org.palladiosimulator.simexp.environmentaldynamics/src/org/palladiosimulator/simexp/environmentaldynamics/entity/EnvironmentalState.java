package org.palladiosimulator.simexp.environmentaldynamics.entity;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.StateImpl;

public class EnvironmentalState extends StateImpl implements PerceivableEnvironmentalState {

	private final PerceivedValue<?> value;
	
	protected EnvironmentalState(PerceivedValue<?> value) {
		this.value = value;
	}
	
	public static EnvironmentalState get(PerceivedValue<?> value) {
		return new EnvironmentalState(value);
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	@Override
	public PerceivedValue<?> getValue() {
		return value;
	}
	
}
