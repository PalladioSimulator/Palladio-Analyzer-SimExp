package de.fzi.srp.core.process.markovian;

import de.fzi.srp.core.model.markovmodel.samplemodel.Sample;
import de.fzi.srp.distribution.function.ProbabilityMassFunction;

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
