package org.palladiosimulator.simexp.pcm.perceiption;

import org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalState;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedValue;

public class PcmEnvironmentalState extends EnvironmentalState implements PcmModelChange {

	private final PcmModelChange decoratedModelChange;
	
	public PcmEnvironmentalState(PcmModelChange decoratedModelChange, PerceivedValue<?> value) {
		super(value);
		this.decoratedModelChange = decoratedModelChange;
	}
	
	@Override
	public void apply(PerceivedValue<?> change) {
		decoratedModelChange.apply(change);
	}
	
}
