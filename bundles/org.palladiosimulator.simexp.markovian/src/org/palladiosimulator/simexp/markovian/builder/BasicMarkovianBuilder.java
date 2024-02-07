package org.palladiosimulator.simexp.markovian.builder;

import java.util.Objects;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator;
import org.palladiosimulator.simexp.markovian.type.BasicMarkovian;

public class BasicMarkovianBuilder<S, A, R> implements
        BasicMarkovianBuilderTemplate<BasicMarkovianBuilder<S, A, R>, S, A>, Builder<BasicMarkovian<S, A, R>> {

    private ProbabilityMassFunction<State<S>> initialDist;
    private StateSpaceNavigator<S, A> stateSpaceNavigator;

    private BasicMarkovianBuilder() {
    }

    public static <S, A, R> BasicMarkovianBuilder<S, A, R> createBasicMarkovian() {
        return new BasicMarkovianBuilder<>();
    }

    @Override
    public BasicMarkovianBuilder<S, A, R> createStateSpaceNavigator(StateSpaceNavigator<S, A> stateSpaceNavigator) {
        this.stateSpaceNavigator = stateSpaceNavigator;
        return this;
    }

    @Override
    public BasicMarkovianBuilder<S, A, R> withInitialStateDistribution(
            ProbabilityMassFunction<State<S>> initialDistribution) {
        initialDist = initialDistribution;
        return this;
    }

    @Override
    public BasicMarkovian<S, A, R> build() {
        // TODO Exception handling
        Objects.requireNonNull(initialDist, "");
        Objects.requireNonNull(stateSpaceNavigator, "");

        return new BasicMarkovian<>(initialDist, stateSpaceNavigator);
    }
}
