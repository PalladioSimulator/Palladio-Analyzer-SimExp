package de.fzi.srp.core.process.markovian;

import de.fzi.srp.core.model.markovmodel.samplemodel.Sample;
import de.fzi.srp.distribution.function.ProbabilityMassFunction;

public interface Markovian {

	public void drawSample(Sample sample);
	
	public Sample determineInitialState();
	
	public ProbabilityMassFunction getInitialStateDistribution(); 
	
}
