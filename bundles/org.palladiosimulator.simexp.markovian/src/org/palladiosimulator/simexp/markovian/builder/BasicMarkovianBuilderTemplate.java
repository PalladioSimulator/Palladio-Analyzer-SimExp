package org.palladiosimulator.simexp.markovian.builder;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator;

public interface BasicMarkovianBuilderTemplate<T> {
	
	public T createStateSpaceNavigator(StateSpaceNavigator stateSpaceNavigator);
	
	public T withInitialStateDistribution(ProbabilityMassFunction initialDistribution);
}
