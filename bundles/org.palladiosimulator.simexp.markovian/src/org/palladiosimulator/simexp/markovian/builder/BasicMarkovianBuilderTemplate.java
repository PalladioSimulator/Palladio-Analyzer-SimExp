package org.palladiosimulator.simexp.markovian.builder;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.markovian.builder.MarkovianBuilder.MDPBuilder;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator;

public interface BasicMarkovianBuilderTemplate<T> {
	
	public MDPBuilder<T> createStateSpaceNavigator(StateSpaceNavigator stateSpaceNavigator);
	
	public MDPBuilder<T> withInitialStateDistribution(ProbabilityMassFunction<T> initialDistribution);
}
