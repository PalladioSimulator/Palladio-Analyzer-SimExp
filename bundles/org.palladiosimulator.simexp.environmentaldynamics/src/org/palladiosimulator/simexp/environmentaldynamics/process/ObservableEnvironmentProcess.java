package org.palladiosimulator.simexp.environmentaldynamics.process;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.environmentaldynamics.entity.DerivableEnvironmentalDynamic;
import org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalState;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.markovian.builder.MarkovianBuilder;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator;
import org.palladiosimulator.simexp.markovian.type.Markovian;

public class ObservableEnvironmentProcess extends EnvironmentProcess {

	public ObservableEnvironmentProcess(MarkovModel model, ProbabilityMassFunction initialDistribution) {
		super(model, initialDistribution);
	}
	
	public ObservableEnvironmentProcess(DerivableEnvironmentalDynamic dynamics, ProbabilityMassFunction initialDistribution) {
		super(dynamics, initialDistribution);
	}

	@Override
	protected Markovian buildMarkovian(StateSpaceNavigator environmentalDynamics, ProbabilityMassFunction initialDistribution) {
		return MarkovianBuilder.createMarkovChain()
							   .createStateSpaceNavigator(environmentalDynamics)
							   .withInitialStateDistribution(initialDistribution)
							   .build();
	}

	@Override
	public PerceivableEnvironmentalState determineNextGiven(PerceivableEnvironmentalState last) {
		return (PerceivableEnvironmentalState) determineNextSampleGiven((EnvironmentalState) last).getNext();
	}

	@Override
	public PerceivableEnvironmentalState determineInitial() {
		return (PerceivableEnvironmentalState) sampler.drawInitialSample().getCurrent();
	}

}
