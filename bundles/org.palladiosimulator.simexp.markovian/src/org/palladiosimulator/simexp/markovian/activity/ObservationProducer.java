package org.palladiosimulator.simexp.markovian.activity;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Observation;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

public interface ObservationProducer<T> {

	public Observation<T> produceObservationGiven(State<T> emittingState);
}
