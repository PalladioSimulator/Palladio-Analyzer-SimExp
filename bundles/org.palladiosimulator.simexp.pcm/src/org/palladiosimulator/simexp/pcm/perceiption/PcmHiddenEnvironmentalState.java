package org.palladiosimulator.simexp.pcm.perceiption;

import org.palladiosimulator.simexp.distribution.function.ProbabilityDistributionFunction;
import org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalState;
import org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalStateObservation;
import org.palladiosimulator.simexp.environmentaldynamics.entity.HiddenEnvironmentalState;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedValue;

public class PcmHiddenEnvironmentalState extends HiddenEnvironmentalState implements PcmModelChange {

	private final PcmModelChange decoratedModelChange;

	public PcmHiddenEnvironmentalState(PcmModelChange decoratedModelChange, EnvironmentalState trueState,
			ProbabilityDistributionFunction<EnvironmentalStateObservation> obsDistribution) {
		super(trueState, obsDistribution);
		this.decoratedModelChange = decoratedModelChange;
	}

	@Override
	public void apply(PerceivedValue<?> change) {
		decoratedModelChange.apply(change);
	}

}
