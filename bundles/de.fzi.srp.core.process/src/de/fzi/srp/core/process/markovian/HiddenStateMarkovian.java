package de.fzi.srp.core.process.markovian;

import de.fzi.srp.core.model.markovmodel.markoventity.Observation;
import de.fzi.srp.core.model.markovmodel.samplemodel.Sample;
import de.fzi.srp.core.process.markovian.activity.ObservationProducer;

public class HiddenStateMarkovian extends MarkovianDecorator {

	private final ObservationProducer obsDistribution;
	
	public HiddenStateMarkovian(Markovian decoratedMarkovian, ObservationProducer obsDistribution) {
		super(decoratedMarkovian);
		this.obsDistribution = obsDistribution;
	}

	@Override
	public void drawSample(Sample sample) {
		decoratedMarkovian.drawSample(sample);
		produceObservation(sample);
	}

	private void produceObservation(Sample sample) {
		Observation<?> result = obsDistribution.produceObservationGiven(sample.getNext());
		sample.setObservation(result);
	}
	
	@Override
	public Sample determineInitialState() {
		Sample initial = decoratedMarkovian.determineInitialState();
		produceObservationForInitial(initial);
		return initial;
	}

	private void produceObservationForInitial(Sample sample) {
		//TODO this could be better solved...
		Observation<?> result = obsDistribution.produceObservationGiven(sample.getCurrent());
		sample.getCurrent().getProduces().add(result);
	}
	
}
