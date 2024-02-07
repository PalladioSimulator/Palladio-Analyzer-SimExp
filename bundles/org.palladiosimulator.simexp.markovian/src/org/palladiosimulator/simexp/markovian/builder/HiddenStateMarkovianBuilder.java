package org.palladiosimulator.simexp.markovian.builder;

import java.util.Objects;

import org.palladiosimulator.simexp.markovian.activity.ObservationProducer;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.type.HiddenStateMarkovian;
import org.palladiosimulator.simexp.markovian.type.Markovian;

public class HiddenStateMarkovianBuilder<S, A, R>
        implements HiddenStateMarkovianBuilderTemplate<HiddenStateMarkovianBuilder<S, A, R>, S>,
        Builder<HiddenStateMarkovian<S, A, R, State<S>>> {

    private ObservationProducer<S> obsHandler;
    private Markovian<S, A, R, State<S>> decoratedMarkovian;

    private HiddenStateMarkovianBuilder() {

    }

    public static <S, A, R> HiddenStateMarkovianBuilder<S, A, R> createHiddenStateMarkovianBuilder() {
        return new HiddenStateMarkovianBuilder<>();
    }

    public HiddenStateMarkovianBuilder<S, A, R> decorates(Markovian<S, A, R, State<S>> decoratedMarkovian) {
        this.decoratedMarkovian = decoratedMarkovian;
        return this;
    }

    @Override
    public HiddenStateMarkovianBuilder<S, A, R> handleObservationsWith(ObservationProducer<S> obsHandler) {
        this.obsHandler = obsHandler;
        return this;
    }

    @Override
    public HiddenStateMarkovian<S, A, R, State<S>> build() {
        // TODO Exception handling
        Objects.requireNonNull(obsHandler, "");
        Objects.requireNonNull(decoratedMarkovian, "");

        // public HiddenStateMarkovian(Markovian<T> decoratedMarkovian, ObservationProducer<T>
        // obsDistribution) {
        return new HiddenStateMarkovian<>(decoratedMarkovian, obsHandler);
    }
}
