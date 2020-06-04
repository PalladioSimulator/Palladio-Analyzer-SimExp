package org.palladiosimulator.simexp.markovian.builder;

import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator;

import de.fzi.srp.distribution.function.ProbabilityMassFunction;

public interface BasicMarkovianBuilderTemplate<T> {
	
	public T createStateSpaceNavigator(StateSpaceNavigator stateSpaceNavigator);
	
	public T withInitialStateDistribution(ProbabilityMassFunction initialDistribution);
}
