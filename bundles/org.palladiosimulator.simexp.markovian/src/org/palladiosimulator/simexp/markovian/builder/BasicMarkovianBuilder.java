package org.palladiosimulator.simexp.markovian.builder;

import java.util.Objects;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator;
import org.palladiosimulator.simexp.markovian.type.BasicMarkovian;

public class BasicMarkovianBuilder<A, R>
        implements BasicMarkovianBuilderTemplate<BasicMarkovianBuilder<A, R>, A>, Builder<BasicMarkovian<A, R>> {

    private ProbabilityMassFunction<State> initialDist;
    private StateSpaceNavigator<A> stateSpaceNavigator;

    private BasicMarkovianBuilder() {
    }

    public static <A, R> BasicMarkovianBuilder<A, R> createBasicMarkovian() {
        return new BasicMarkovianBuilder<>();
    }

    @Override
    public BasicMarkovianBuilder<A, R> createStateSpaceNavigator(StateSpaceNavigator<A> stateSpaceNavigator) {
        this.stateSpaceNavigator = stateSpaceNavigator;
        return this;
    }

    @Override
    public BasicMarkovianBuilder<A, R> withInitialStateDistribution(
            ProbabilityMassFunction<State> initialDistribution) {
        initialDist = initialDistribution;
        return this;
    }

    @Override
    public BasicMarkovian<A, R> build() {
        // TODO Exception handling
        Objects.requireNonNull(initialDist, "");
        Objects.requireNonNull(stateSpaceNavigator, "");

        return new BasicMarkovian<>(initialDist, stateSpaceNavigator);
    }
}
