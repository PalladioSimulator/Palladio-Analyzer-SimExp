package org.palladiosimulator.simexp.markovian.type;

import org.palladiosimulator.simexp.markovian.activity.ObservationProducer;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Observation;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public class HiddenStateMarkovian<S, A, R> extends MarkovianDecorator<S, A, R> {

    private final ObservationProducer<S> obsDistribution;

    public HiddenStateMarkovian(Markovian<S, A, R> decoratedMarkovian, ObservationProducer<S> obsDistribution) {
        super(decoratedMarkovian);
        this.obsDistribution = obsDistribution;
    }

    @Override
    public void drawSample(Sample<S, A, R, S> sample) {
        decoratedMarkovian.drawSample(sample);
        produceObservation(sample);
    }

    private void produceObservation(Sample<S, A, R, S> sample) {
        // TODO: replace with Observation<State<S>>
        Observation result = obsDistribution.produceObservationGiven(sample.getNext());
        sample.setObservation(result);
    }

    @Override
    public Sample<S, A, R, S> determineInitialState() {
        Sample<S, A, R, S> initial = decoratedMarkovian.determineInitialState();
        produceObservationForInitial(initial);
        return initial;
    }

    private void produceObservationForInitial(Sample<S, A, R, S> sample) {
        // TODO this could be better solved...
        State<S> currentState = sample.getCurrent();
        Observation<S> result = obsDistribution.produceObservationGiven(currentState);
        sample.setObservation(result);
        /*
         * currentState.getProduces().add(result);
         */
        result.getObserved()
            .add(currentState);
    }

}
