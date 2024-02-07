package org.palladiosimulator.simexp.environmentaldynamics.process;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.environmentaldynamics.entity.DerivableEnvironmentalDynamic;
import org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalState;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.markovian.builder.MarkovianBuilder;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator;
import org.palladiosimulator.simexp.markovian.type.Markovian;

public class ObservableEnvironmentProcess<S, A, Aa extends Action<A>, R> extends EnvironmentProcess<S, A, R> {

    public ObservableEnvironmentProcess(MarkovModel<S, A, R> model,
            ProbabilityMassFunction<State<S>> initialDistribution) {
        super(buildMarkovian(buildEnvironmentalDynamics(model), initialDistribution), model, initialDistribution);
    }

    public ObservableEnvironmentProcess(DerivableEnvironmentalDynamic<S, A> dynamics,
            ProbabilityMassFunction<State<S>> initialDistribution) {
        super(buildMarkovian(dynamics, initialDistribution), dynamics, initialDistribution);
    }

    private static <S, A, Aa extends Action<A>, R> Markovian<S, A, R> buildMarkovian(
            StateSpaceNavigator<S, A> environmentalDynamics, ProbabilityMassFunction<State<S>> initialDistribution) {
        MarkovianBuilder<S, A, Aa, R>.MarkovChainBuilder markovChain = MarkovianBuilder
            .<S, A, Aa, R> createMarkovChain();
        return markovChain.createStateSpaceNavigator(environmentalDynamics)
            .withInitialStateDistribution(initialDistribution)
            .build();
    }

    @Override
    public PerceivableEnvironmentalState determineNextGiven(PerceivableEnvironmentalState last) {
        return (PerceivableEnvironmentalState) determineNextSampleGiven((EnvironmentalState<S>) last).getNext();
    }

    @Override
    public PerceivableEnvironmentalState determineInitial() {
        return (PerceivableEnvironmentalState) sampler.drawInitialSample()
            .getCurrent();
    }

}
