package org.palladiosimulator.simexp.pcm.prism.generator;

import org.palladiosimulator.simexp.pcm.prism.entity.PrismContext;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;

public interface PrismGenerator {
	
	public PrismContext generate(PcmSelfAdaptiveSystemState sasState, PrismSimulatedMeasurementSpec prismSpec);
}
