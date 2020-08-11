package org.palladiosimulator.simexp.pcm.perceiption;

import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedValue;

public interface PcmModelChange {

	public void apply(PerceivedValue<?> change);
	
}
