package de.fzi.srp.core.process.config;

import java.util.Optional;

import de.fzi.srp.core.model.markovmodel.markoventity.MarkovModel;
import de.fzi.srp.core.model.markovmodel.samplemodel.SampleModel;
import de.fzi.srp.core.process.termination.TerminationCriterion;

public class MarkovProcessConfig {
	public int horizonLength;
	public MarkovModel markovModel;
	public TerminationCriterion criterion;
	private SampleModel sampleModel;
	
	public Optional<SampleModel> getSampleModel() {
		return sampleModel == null ? Optional.empty() : Optional.of(sampleModel);
	}
}
