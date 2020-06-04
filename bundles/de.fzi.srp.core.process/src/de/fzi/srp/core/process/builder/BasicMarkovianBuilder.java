package de.fzi.srp.core.process.builder;

import java.util.Objects;

import de.fzi.srp.core.process.markovian.BasicMarkovian;
import de.fzi.srp.core.process.statespace.StateSpaceNavigator;
import de.fzi.srp.distribution.function.ProbabilityMassFunction;

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
