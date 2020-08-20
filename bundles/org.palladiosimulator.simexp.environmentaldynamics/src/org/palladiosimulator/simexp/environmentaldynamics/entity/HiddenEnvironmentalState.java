package org.palladiosimulator.simexp.environmentaldynamics.entity;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.StateImpl;

public class HiddenEnvironmentalState extends StateImpl {

	private final EnvironmentalState trueState;
	
	protected HiddenEnvironmentalState(EnvironmentalState trueState) { 
		this.trueState = trueState;
	}
	
	public static HiddenEnvironmentalState get(EnvironmentalState trueState) {
		return new HiddenEnvironmentalState(trueState);
	}
	
	public EnvironmentalState getTrueState() {
		return trueState;
	}
	
}
