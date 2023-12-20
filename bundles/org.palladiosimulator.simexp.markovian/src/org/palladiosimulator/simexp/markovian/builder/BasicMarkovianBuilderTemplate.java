package org.palladiosimulator.simexp.markovian.builder;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator;

public interface BasicMarkovianBuilderTemplate<T, S, A> {

    public T createStateSpaceNavigator(StateSpaceNavigator<S, A> stateSpaceNavigator);

    public T withInitialStateDistribution(ProbabilityMassFunction<State<S>> initialDistribution);
}
