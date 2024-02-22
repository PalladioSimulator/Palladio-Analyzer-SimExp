package org.palladiosimulator.simexp.markovian.builder;

import java.util.Objects;

import org.palladiosimulator.simexp.markovian.activity.ObservationProducer;
import org.palladiosimulator.simexp.markovian.type.HiddenStateMarkovian;
import org.palladiosimulator.simexp.markovian.type.Markovian;

public class HiddenStateMarkovianBuilder<A, R> implements
        HiddenStateMarkovianBuilderTemplate<HiddenStateMarkovianBuilder<A, R>>, Builder<HiddenStateMarkovian<A, R>> {

    private ObservationProducer obsHandler;
    private Markovian<A, R> decoratedMarkovian;

    private HiddenStateMarkovianBuilder() {

    }

    public static <A, R> HiddenStateMarkovianBuilder<A, R> createHiddenStateMarkovianBuilder() {
        return new HiddenStateMarkovianBuilder<>();
    }

    public HiddenStateMarkovianBuilder<A, R> decorates(Markovian<A, R> decoratedMarkovian) {
        this.decoratedMarkovian = decoratedMarkovian;
        return this;
    }

    @Override
    public HiddenStateMarkovianBuilder<A, R> handleObservationsWith(ObservationProducer obsHandler) {
        this.obsHandler = obsHandler;
        return this;
    }

    @Override
    public HiddenStateMarkovian<A, R> build() {
        // TODO Exception handling
        Objects.requireNonNull(obsHandler, "");
        Objects.requireNonNull(decoratedMarkovian, "");

        // public HiddenStateMarkovian(Markovian<T> decoratedMarkovian, ObservationProducer<T>
        // obsDistribution) {
        return new HiddenStateMarkovian<>(decoratedMarkovian, obsHandler);
    }
}
