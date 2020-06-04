package org.palladiosimulator.simexp.markovian.type;

import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

import de.fzi.srp.distribution.function.ProbabilityMassFunction;

public interface Markovian {

	public void drawSample(Sample sample);
	
	public Sample determineInitialState();
	
	public ProbabilityMassFunction getInitialStateDistribution(); 
	
}
