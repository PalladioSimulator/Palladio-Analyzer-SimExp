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

public class ObservableEnvironmentProcess<A, Aa extends Action<A>, R, V> extends EnvironmentProcess<A, R, V> {

    public ObservableEnvironmentProcess(MarkovModel<A, R> model, ProbabilityMassFunction<State> initialDistribution) {
        super(buildMarkovian(buildEnvironmentalDynamics(model), initialDistribution), model, initialDistribution);
    }

    public ObservableEnvironmentProcess(DerivableEnvironmentalDynamic<A> dynamics,
            ProbabilityMassFunction<State> initialDistribution) {
        super(buildMarkovian(dynamics, initialDistribution), dynamics, initialDistribution);
    }

    private static <A, Aa extends Action<A>, R> Markovian<A, R> buildMarkovian(
            StateSpaceNavigator<A> environmentalDynamics, ProbabilityMassFunction<State> initialDistribution) {
        MarkovianBuilder<A, Aa, R>.MarkovChainBuilder markovChain = MarkovianBuilder.<A, Aa, R> createMarkovChain();
        return markovChain.createStateSpaceNavigator(environmentalDynamics)
            .withInitialStateDistribution(initialDistribution)
            .build();
    }

    @Override
    public PerceivableEnvironmentalState<V> determineNextGiven(PerceivableEnvironmentalState<V> last) {
        return (PerceivableEnvironmentalState<V>) determineNextSampleGiven((EnvironmentalState<V>) last).getNext();
    }

    @Override
    public PerceivableEnvironmentalState<V> determineInitial() {
        return (PerceivableEnvironmentalState<V>) sampler.drawInitialSample()
            .getCurrent();
    }

}
