package org.palladiosimulator.simexp.markovian.type;

import static org.palladiosimulator.simexp.markovian.access.SampleModelAccessor.createInitialSample;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator.NavigationContext;

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
