package org.palladiosimulator.simexp.pcm.perceiption;

import org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalState;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedValue;

//TODO refactor to EnvironmentToPcmElementBinder
public class PcmEnvironmentalState extends EnvironmentalState implements PcmModelChange {

	private final PcmModelChange decoratedModelChange;
	
	public PcmEnvironmentalState(PcmModelChange decoratedModelChange, PerceivedValue<?> value) {
		super(value, false, false);
		this.decoratedModelChange = decoratedModelChange;
	}
	
	@Override
	public void apply(PerceivedValue<?> change) {
		decoratedModelChange.apply(change);
	}
	
}
