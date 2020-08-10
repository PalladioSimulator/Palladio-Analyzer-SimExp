package org.palladiosimulator.simexp.markovian.type;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public interface Markovian {

	public void drawSample(Sample sample);
	
	public Sample determineInitialState();
	
	public ProbabilityMassFunction getInitialStateDistribution(); 
	
}
