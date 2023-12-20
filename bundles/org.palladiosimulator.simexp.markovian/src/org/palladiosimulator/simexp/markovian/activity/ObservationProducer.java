package org.palladiosimulator.simexp.markovian.activity;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Observation;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

public interface ObservationProducer<S> {

    public Observation<S> produceObservationGiven(State<S> emittingState);
}
