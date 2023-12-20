package org.palladiosimulator.simexp.markovian.builder;

import org.palladiosimulator.simexp.markovian.activity.ObservationProducer;

public interface HiddenStateMarkovianBuilderTemplate<T, S> {

    public T handleObservationsWith(ObservationProducer<S> obsHandler);
}
