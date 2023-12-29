package org.palladiosimulator.simexp.markovian.builder;

import java.util.Set;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.markovian.activity.ObservationProducer;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.activity.RewardReceiver;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator;
import org.palladiosimulator.simexp.markovian.type.Markovian;

public class MarkovianBuilder<S, A, Aa extends Action<A>, R> {

    public class MarkovChainBuilder
            implements BasicMarkovianBuilderTemplate<MarkovChainBuilder, S, A>, Builder<Markovian<S, A, R>> {

        @Override
        public Markovian<S, A, R> build() {
            return basicBuilder.build();
        }

        @Override
        public MarkovChainBuilder createStateSpaceNavigator(StateSpaceNavigator<S, A> stateSpaceNavigator) {
            basicBuilder.createStateSpaceNavigator(stateSpaceNavigator);
            return this;
        }

        @Override
        public MarkovChainBuilder withInitialStateDistribution(ProbabilityMassFunction<State<S>> initialDistribution) {
            basicBuilder.withInitialStateDistribution(initialDistribution);
            return this;
        }
    }

    public class HMMBuilder implements BasicMarkovianBuilderTemplate<HMMBuilder, S, A>,
            HiddenStateMarkovianBuilderTemplate<HMMBuilder, S>, Builder<Markovian<S, A, R>> {

        @Override
        public Markovian<S, A, R> build() {
            hiddenBuilder.decorates(basicBuilder.build());
            return hiddenBuilder.build();
        }

        @Override
        public HMMBuilder handleObservationsWith(ObservationProducer<S> obsHandler) {
            hiddenBuilder.handleObservationsWith(obsHandler);
            return this;
        }

        @Override
        public HMMBuilder createStateSpaceNavigator(StateSpaceNavigator<S, A> stateSpaceNavigator) {
            basicBuilder.createStateSpaceNavigator(stateSpaceNavigator);
            return this;
        }

        @Override
        public HMMBuilder withInitialStateDistribution(ProbabilityMassFunction<State<S>> initialDistribution) {
            basicBuilder.withInitialStateDistribution(initialDistribution);
            return this;
        }
    }

    public class MDPBuilder implements BasicMarkovianBuilderTemplate<MDPBuilder, S, A>,
            DecisionBasedMarkovionBuilderTemplate<MDPBuilder, S, A, Aa, R>, Builder<Markovian<S, A, R>> {

        @Override
        public Markovian<S, A, R> build() {
            decisionBuilder.decorates(basicBuilder.build());
            return decisionBuilder.build();
        }

        @Override
        public MDPBuilder calculateRewardWith(RewardReceiver<S, A, R> rewardCalc) {
            decisionBuilder.calculateRewardWith(rewardCalc);
            return this;
        }

        @Override
        public MDPBuilder withActionSpace(Set<Aa> actions) {
            decisionBuilder.withActionSpace(actions);
            return this;
        }

        @Override
        public MDPBuilder selectActionsAccordingTo(Policy<S, A, Aa> policy) {
            decisionBuilder.selectActionsAccordingTo(policy);
            return this;
        }

        @Override
        public MDPBuilder createStateSpaceNavigator(StateSpaceNavigator<S, A> stateSpaceNavigator) {
            basicBuilder.createStateSpaceNavigator(stateSpaceNavigator);
            return this;
        }

        @Override
        public MDPBuilder withInitialStateDistribution(ProbabilityMassFunction<State<S>> initialDistribution) {
            basicBuilder.withInitialStateDistribution(initialDistribution);
            return this;
        }
    }

    public class POMDPBuilder implements BasicMarkovianBuilderTemplate<POMDPBuilder, S, A>,
            DecisionBasedMarkovionBuilderTemplate<POMDPBuilder, S, A, Aa, R>,
            HiddenStateMarkovianBuilderTemplate<POMDPBuilder, S>, Builder<Markovian<S, A, R>> {

        @Override
        public Markovian<S, A, R> build() {
            decisionBuilder.decorates(basicBuilder.build());
            hiddenBuilder.decorates(decisionBuilder.build());
            return hiddenBuilder.build();
        }

        @Override
        public POMDPBuilder handleObservationsWith(ObservationProducer<S> obsHandler) {
            hiddenBuilder.handleObservationsWith(obsHandler);
            return this;
        }

        @Override
        public POMDPBuilder calculateRewardWith(RewardReceiver<S, A, R> rewardCalc) {
            decisionBuilder.calculateRewardWith(rewardCalc);
            return this;
        }

        @Override
        public POMDPBuilder withActionSpace(Set<Aa> actions) {
            decisionBuilder.withActionSpace(actions);
            return this;
        }

        @Override
        public POMDPBuilder selectActionsAccordingTo(Policy<S, A, Aa> policy) {
            decisionBuilder.selectActionsAccordingTo(policy);
            return this;
        }

        @Override
        public POMDPBuilder createStateSpaceNavigator(StateSpaceNavigator<S, A> stateSpaceNavigator) {
            basicBuilder.createStateSpaceNavigator(stateSpaceNavigator);
            return this;
        }

        @Override
        public POMDPBuilder withInitialStateDistribution(ProbabilityMassFunction<State<S>> initialDistribution) {
            basicBuilder.withInitialStateDistribution(initialDistribution);
            return this;
        }
    }

    private BasicMarkovianBuilder<S, A, R> basicBuilder;
    private HiddenStateMarkovianBuilder<S, A, R> hiddenBuilder;
    private DecisionBasedMarkovianBuilder<S, A, Aa, R> decisionBuilder;

    private MarkovianBuilder() {
        this.basicBuilder = BasicMarkovianBuilder.createBasicMarkovian();
        this.hiddenBuilder = HiddenStateMarkovianBuilder.createHiddenStateMarkovianBuilder();
        this.decisionBuilder = DecisionBasedMarkovianBuilder.createDecisionBasedMarkovianBuilder();
    }

    private static <S, A, Aa extends Action<A>, R> MarkovianBuilder<S, A, Aa, R> createBuilder() {
        return new MarkovianBuilder<>();
    }

    public static <S, A, Aa extends Action<A>, R> MarkovianBuilder<S, A, Aa, R>.MarkovChainBuilder createMarkovChain() {
        return MarkovianBuilder.<S, A, Aa, R> createBuilder().new MarkovChainBuilder();
    }

    public static <S, A, Aa extends Action<A>, R> MarkovianBuilder<S, A, Aa, R>.HMMBuilder createHiddenMarkovModel() {
        return MarkovianBuilder.<S, A, Aa, R> createBuilder().new HMMBuilder();
    }

    public static <S, A, Aa extends Action<A>, R> MarkovianBuilder<S, A, Aa, R>.MDPBuilder createMarkovDecisionProcess() {
        return MarkovianBuilder.<S, A, Aa, R> createBuilder().new MDPBuilder();
    }

    public static <S, A, Aa extends Action<A>, R> MarkovianBuilder<S, A, Aa, R>.POMDPBuilder createPartiallyObservableMDP() {
        return MarkovianBuilder.<S, A, Aa, R> createBuilder().new POMDPBuilder();
    }
}
