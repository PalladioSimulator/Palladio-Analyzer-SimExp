package org.palladiosimulator.simexp.markovian.builder;

import java.util.Objects;

import org.palladiosimulator.simexp.markovian.activity.ObservationProducer;
import org.palladiosimulator.simexp.markovian.type.HiddenStateMarkovian;
import org.palladiosimulator.simexp.markovian.type.Markovian;

public class HiddenStateMarkovianBuilder <T> implements HiddenStateMarkovianBuilderTemplate<T>, Builder<HiddenStateMarkovian<T>> {
	
	private ObservationProducer<T> obsHandler;
	private Markovian<T> decoratedMarkovian;
	
	private HiddenStateMarkovianBuilder() {
		
	}
	
	public static <T> HiddenStateMarkovianBuilder<T> createHiddenStateMarkovianBuilder() {
		return new HiddenStateMarkovianBuilder<T>();
	}
	
	public HiddenStateMarkovianBuilder<T> decorates(Markovian<T> decoratedMarkovian) {
		this.decoratedMarkovian = decoratedMarkovian;
		return this;
	}
	
	@Override
	public HiddenStateMarkovianBuilder<T> handleObservationsWith(ObservationProducer<T> obsHandler) {
		this.obsHandler = obsHandler;
		return this;
	}
	
	@Override
	public HiddenStateMarkovian<T> build() {
		//TODO Exception handling
		Objects.requireNonNull(obsHandler, "");
		Objects.requireNonNull(decoratedMarkovian, "");
		
		//public HiddenStateMarkovian(Markovian<T> decoratedMarkovian, ObservationProducer<T> obsDistribution) {
		return new HiddenStateMarkovian<>(decoratedMarkovian, obsHandler);
	}
}
