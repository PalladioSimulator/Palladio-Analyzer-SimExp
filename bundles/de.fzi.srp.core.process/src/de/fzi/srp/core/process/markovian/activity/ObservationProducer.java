package de.fzi.srp.core.process.markovian.activity;

import de.fzi.srp.core.model.markovmodel.markoventity.Observation;
import de.fzi.srp.core.model.markovmodel.markoventity.State;

public interface ObservationProducer {

	public Observation<?> produceObservationGiven(State emittingState);
}
