package org.palladiosimulator.simexp.markovian.type;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public interface Markovian<T> {

	public void drawSample(Sample<T> sample);
	
	public Sample<T> determineInitialState();
	
	public ProbabilityMassFunction<T> getInitialStateDistribution(); 
	
}
