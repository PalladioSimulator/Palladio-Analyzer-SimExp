package org.palladiosimulator.simexp.environmentaldynamics.entity;

import org.palladiosimulator.simexp.distribution.function.ProbabilityDistributionFunction;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.StateImpl;

public class HiddenEnvironmentalState extends StateImpl {

	private final EnvironmentalState trueState;
	private final ProbabilityDistributionFunction<EnvironmentalStateObservation> obsDistribution;
	
	protected HiddenEnvironmentalState(EnvironmentalState trueState, 
									   ProbabilityDistributionFunction<EnvironmentalStateObservation> obsDistribution) {
		this.obsDistribution = obsDistribution;
		this.trueState = trueState;
	}
	
	public static HiddenEnvironmentalState get(ProbabilityDistributionFunction<EnvironmentalStateObservation> obsDistribution, EnvironmentalState trueState) {
		return new HiddenEnvironmentalState(trueState, obsDistribution);
	}
	
	public EnvironmentalStateObservation produceObservation() {
		return obsDistribution.drawSample();
	}
	
	public EnvironmentalState getTrueState() {
		return trueState;
	}
	
}
