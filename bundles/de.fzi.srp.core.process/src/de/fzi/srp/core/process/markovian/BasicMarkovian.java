package de.fzi.srp.core.process.markovian;

import static de.fzi.srp.core.process.access.SampleModelAccessor.createInitialSample;

import de.fzi.srp.core.model.markovmodel.markoventity.State;
import de.fzi.srp.core.model.markovmodel.samplemodel.Sample;
import de.fzi.srp.core.process.statespace.StateSpaceNavigator;
import de.fzi.srp.core.process.statespace.StateSpaceNavigator.NavigationContext;
import de.fzi.srp.distribution.function.ProbabilityMassFunction;

public class BasicMarkovian implements Markovian {

	private final ProbabilityMassFunction initialStateDistribution;
	private final StateSpaceNavigator stateSpaceNavigator;
	
	public BasicMarkovian(ProbabilityMassFunction initialStateDistribution, 
						  StateSpaceNavigator stateSpaceNavigator) {
		this.initialStateDistribution = initialStateDistribution;
		this.stateSpaceNavigator = stateSpaceNavigator;
	}

	@Override
	public void drawSample(Sample sample) {
		NavigationContext context = NavigationContext.of(sample);
		sample.setNext(stateSpaceNavigator.navigate(context));
	}

	@Override
	public Sample determineInitialState() {
		return createInitialSample((State) initialStateDistribution.drawSample().getValue());
	}

	@Override
	public ProbabilityMassFunction getInitialStateDistribution() {
		return initialStateDistribution;
	}

}
