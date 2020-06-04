package org.palladiosimulator.simexp.markovian.config;

import java.util.Optional;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.SampleModel;
import org.palladiosimulator.simexp.markovian.termination.TerminationCriterion;

public class MarkovProcessConfig {
	public int horizonLength;
	public MarkovModel markovModel;
	public TerminationCriterion criterion;
	private SampleModel sampleModel;
	
	public Optional<SampleModel> getSampleModel() {
		return sampleModel == null ? Optional.empty() : Optional.of(sampleModel);
	}
}
