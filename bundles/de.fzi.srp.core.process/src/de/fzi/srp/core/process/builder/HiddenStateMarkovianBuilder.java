package de.fzi.srp.core.process.builder;

import java.util.Objects;

import de.fzi.srp.core.process.markovian.HiddenStateMarkovian;
import de.fzi.srp.core.process.markovian.Markovian;
import de.fzi.srp.core.process.markovian.activity.ObservationProducer;

public class HiddenStateMarkovianBuilder implements HiddenStateMarkovianBuilderTemplate<HiddenStateMarkovianBuilder>, Builder<HiddenStateMarkovian> {
	
	private ObservationProducer obsHandler;
	private Markovian decoratedMarkovian;
	
	private HiddenStateMarkovianBuilder() {
		
	}
	
	public static HiddenStateMarkovianBuilder createHiddenStateMarkovianBuilder() {
		return new HiddenStateMarkovianBuilder();
	}
	
	public HiddenStateMarkovianBuilder decorates(Markovian decoratedMarkovian) {
		this.decoratedMarkovian = decoratedMarkovian;
		return this;
	}
	
	@Override
	public HiddenStateMarkovianBuilder handleObservationsWith(ObservationProducer obsHandler) {
		this.obsHandler = obsHandler;
		return this;
	}
	
	@Override
	public HiddenStateMarkovian build() {
		//TODO Exception handling
		Objects.requireNonNull(obsHandler, "");
		Objects.requireNonNull(decoratedMarkovian, "");
		
		return new HiddenStateMarkovian(decoratedMarkovian, obsHandler);
	}
}
