package de.fzi.srp.core.process.builder;

import de.fzi.srp.core.process.markovian.activity.ObservationProducer;

public interface HiddenStateMarkovianBuilderTemplate<T> {
	
	public T handleObservationsWith(ObservationProducer obsHandler);
}
