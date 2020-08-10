package org.palladiosimulator.simexp.markovian.builder;

import java.util.Objects;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator;
import org.palladiosimulator.simexp.markovian.type.BasicMarkovian;

public class BasicMarkovianBuilder implements BasicMarkovianBuilderTemplate<BasicMarkovianBuilder>, Builder<BasicMarkovian> {
	
	private ProbabilityMassFunction initialDist;
	private StateSpaceNavigator stateSpaceNavigator;
	
	private BasicMarkovianBuilder() {

	}
	
	public static BasicMarkovianBuilder createBasicMarkovian() {
		return new BasicMarkovianBuilder();
	}
	
	@Override
	public BasicMarkovianBuilder createStateSpaceNavigator(StateSpaceNavigator stateSpaceNavigator) {
		this.stateSpaceNavigator = stateSpaceNavigator;
		return this;
	}
	
	@Override
	public BasicMarkovianBuilder withInitialStateDistribution(ProbabilityMassFunction initialDistribution) {
		initialDist = initialDistribution;
		return this;
	}
	
	@Override
	public BasicMarkovian build() {
		//TODO Exception handling
		Objects.requireNonNull(initialDist, "");
		Objects.requireNonNull(stateSpaceNavigator, "");
		
		return new BasicMarkovian(initialDist, stateSpaceNavigator);
	}
}
