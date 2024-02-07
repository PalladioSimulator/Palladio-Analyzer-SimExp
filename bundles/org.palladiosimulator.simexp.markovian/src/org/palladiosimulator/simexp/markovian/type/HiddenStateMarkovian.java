package org.palladiosimulator.simexp.markovian.type;

import org.palladiosimulator.simexp.markovian.activity.ObservationProducer;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Observation;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public class HiddenStateMarkovian<S, A, R, O> extends MarkovianDecorator<S, A, R, O> {

    private final ObservationProducer<S> obsDistribution;

    public HiddenStateMarkovian(Markovian<S, A, R, O> decoratedMarkovian, ObservationProducer<S> obsDistribution) {
        super(decoratedMarkovian);
        this.obsDistribution = obsDistribution;
    }

    @Override
    public void drawSample(Sample<S, A, R, O> sample) {
        decoratedMarkovian.drawSample(sample);
        produceObservation(sample);
    }

    private void produceObservation(Sample<S, A, R, O> sample) {
        // TODO: replace with Observation<State<S>>
        Observation result = obsDistribution.produceObservationGiven(sample.getNext());
        sample.setObservation(result);
    }

    @Override
    public Sample<S, A, R, O> determineInitialState() {
        Sample<S, A, R, O> initial = decoratedMarkovian.determineInitialState();
        produceObservationForInitial(initial);
        return initial;
    }

    private void produceObservationForInitial(Sample<S, A, R, O> sample) {
        // TODO this could be better solved...
        Observation<S> result = obsDistribution.produceObservationGiven(sample.getCurrent());
        sample.getCurrent()
            .getProduces()
            .add(result);
    }

}
