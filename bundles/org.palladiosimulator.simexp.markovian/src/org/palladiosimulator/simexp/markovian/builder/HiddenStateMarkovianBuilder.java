package org.palladiosimulator.simexp.markovian.builder;

import java.util.Objects;

import org.palladiosimulator.simexp.markovian.activity.ObservationProducer;
import org.palladiosimulator.simexp.markovian.type.HiddenStateMarkovian;
import org.palladiosimulator.simexp.markovian.type.Markovian;

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
