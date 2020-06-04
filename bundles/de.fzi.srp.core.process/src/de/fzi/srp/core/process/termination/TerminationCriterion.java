package de.fzi.srp.core.process.termination;

import java.util.function.Predicate;

import de.fzi.srp.core.model.markovmodel.samplemodel.SampleModel;

public interface TerminationCriterion {
	public Predicate<SampleModel> isReached();
}
