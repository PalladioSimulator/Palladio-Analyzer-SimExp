package org.palladiosimulator.simexp.markovian.builder;

import org.palladiosimulator.simexp.markovian.activity.ObservationProducer;

public interface HiddenStateMarkovianBuilderTemplate<T> {

    public T handleObservationsWith(ObservationProducer obsHandler);
}
