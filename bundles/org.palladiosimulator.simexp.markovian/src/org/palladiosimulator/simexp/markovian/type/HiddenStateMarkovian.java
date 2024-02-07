package org.palladiosimulator.simexp.markovian.type;

import org.palladiosimulator.simexp.markovian.activity.ObservationProducer;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Observation;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public class HiddenStateMarkovian<A, R> extends MarkovianDecorator<A, R> {

    private final ObservationProducer obsDistribution;

    public HiddenStateMarkovian(Markovian<A, R> decoratedMarkovian, ObservationProducer obsDistribution) {
        super(decoratedMarkovian);
        this.obsDistribution = obsDistribution;
    }

    @Override
    public void drawSample(Sample<A, R> sample) {
        decoratedMarkovian.drawSample(sample);
        produceObservation(sample);
    }

    private void produceObservation(Sample<A, R> sample) {
        Observation result = obsDistribution.produceObservationGiven(sample.getNext());
        sample.setObservation(result);
    }

    @Override
    public Sample<A, R> determineInitialState() {
        Sample<A, R> initial = decoratedMarkovian.determineInitialState();
        produceObservationForInitial(initial);
        return initial;
    }

    private void produceObservationForInitial(Sample<A, R> sample) {
        // TODO this could be better solved...
        State currentState = sample.getCurrent();
        Observation result = obsDistribution.produceObservationGiven(currentState);
        sample.setObservation(result);
        /*
         * currentState.getProduces().add(result);
         */
        result.setObserved(currentState);
    }

}
