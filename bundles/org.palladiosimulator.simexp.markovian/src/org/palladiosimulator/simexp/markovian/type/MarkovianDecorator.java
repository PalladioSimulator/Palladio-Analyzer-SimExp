package org.palladiosimulator.simexp.markovian.type;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public abstract class MarkovianDecorator<T> implements Markovian<T> {

	protected Markovian<T> decoratedMarkovian = null;
	
	public MarkovianDecorator(Markovian<T> decoratedMarkovian) {
		this.decoratedMarkovian = decoratedMarkovian;
	}
	
	@Override
	public Sample<T> determineInitialState() {
		return decoratedMarkovian.determineInitialState();
	}
	
	@Override
	public ProbabilityMassFunction<T> getInitialStateDistribution() {
		return decoratedMarkovian.getInitialStateDistribution();
	}
}
