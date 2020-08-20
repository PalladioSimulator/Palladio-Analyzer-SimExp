package org.palladiosimulator.simexp.pcm.perceiption;

import org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalState;
import org.palladiosimulator.simexp.environmentaldynamics.entity.HiddenEnvironmentalState;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedValue;

public class PcmHiddenEnvironmentalState extends HiddenEnvironmentalState implements PcmModelChange {

	private final PcmModelChange decoratedModelChange;

	public PcmHiddenEnvironmentalState(PcmModelChange decoratedModelChange, EnvironmentalState trueState) {
		super(trueState);
		this.decoratedModelChange = decoratedModelChange;
	}

	@Override
	public void apply(PerceivedValue<?> change) {
		decoratedModelChange.apply(change);
	}

}
