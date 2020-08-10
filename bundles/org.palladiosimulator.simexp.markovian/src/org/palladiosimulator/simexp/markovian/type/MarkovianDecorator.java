package org.palladiosimulator.simexp.markovian.type;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public abstract class MarkovianDecorator implements Markovian {

	protected Markovian decoratedMarkovian = null;
	
	public MarkovianDecorator(Markovian decoratedMarkovian) {
		this.decoratedMarkovian = decoratedMarkovian;
	}
	
	@Override
	public Sample determineInitialState() {
		return decoratedMarkovian.determineInitialState();
	}
	
	@Override
	public ProbabilityMassFunction getInitialStateDistribution() {
		return decoratedMarkovian.getInitialStateDistribution();
	}
}
