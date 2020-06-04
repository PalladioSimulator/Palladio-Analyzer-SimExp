package de.fzi.srp.core.process.builder;

import de.fzi.srp.core.process.statespace.StateSpaceNavigator;
import de.fzi.srp.distribution.function.ProbabilityMassFunction;

public interface BasicMarkovianBuilderTemplate<T> {
	
	public T createStateSpaceNavigator(StateSpaceNavigator stateSpaceNavigator);
	
	public T withInitialStateDistribution(ProbabilityMassFunction initialDistribution);
}
