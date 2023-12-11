package org.palladiosimulator.simexp.markovian.type;

import org.palladiosimulator.simexp.markovian.activity.ObservationProducer;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Observation;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public class HiddenStateMarkovian <T> extends MarkovianDecorator<T> {

	private final ObservationProducer<T> obsDistribution;
	
	public HiddenStateMarkovian(Markovian<T> decoratedMarkovian, ObservationProducer<T> obsDistribution) {
		super(decoratedMarkovian);
		this.obsDistribution = obsDistribution;
	}

	@Override
	public void drawSample(Sample<T> sample) {
		decoratedMarkovian.drawSample(sample);
		produceObservation(sample);
	}

	private void produceObservation(Sample<T> sample) {
		Observation<T> result = obsDistribution.produceObservationGiven(sample.getNext());
		sample.setObservation(result);
	}
	
	@Override
	public Sample<T> determineInitialState() {
		Sample<T> initial = decoratedMarkovian.determineInitialState();
		produceObservationForInitial(initial);
		return initial;
	}

	private void produceObservationForInitial(Sample<T> sample) {
		//TODO this could be better solved...
		Observation<T> result = obsDistribution.produceObservationGiven(sample.getCurrent());
		sample.getCurrent().getProduces().add(result);
	}
	
}
