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

public class MarkovianBuilder<A, Aa extends Action<A>, R> {

    public class MarkovChainBuilder
            implements BasicMarkovianBuilderTemplate<MarkovChainBuilder, A>, Builder<Markovian<A, R>> {

        @Override
        public Markovian<A, R> build() {
            return basicBuilder.build();
        }

        @Override
        public MarkovChainBuilder createStateSpaceNavigator(StateSpaceNavigator<A> stateSpaceNavigator) {
            basicBuilder.createStateSpaceNavigator(stateSpaceNavigator);
            return this;
        }

        @Override
        public MarkovChainBuilder withInitialStateDistribution(ProbabilityMassFunction<State> initialDistribution) {
            basicBuilder.withInitialStateDistribution(initialDistribution);
            return this;
        }
    }

    public class HMMBuilder implements BasicMarkovianBuilderTemplate<HMMBuilder, A>,
            HiddenStateMarkovianBuilderTemplate<HMMBuilder>, Builder<Markovian<A, R>> {

        @Override
        public Markovian<A, R> build() {
            hiddenBuilder.decorates(basicBuilder.build());
            return hiddenBuilder.build();
        }

        @Override
        public HMMBuilder handleObservationsWith(ObservationProducer obsHandler) {
            hiddenBuilder.handleObservationsWith(obsHandler);
            return this;
        }

        @Override
        public HMMBuilder createStateSpaceNavigator(StateSpaceNavigator<A> stateSpaceNavigator) {
            basicBuilder.createStateSpaceNavigator(stateSpaceNavigator);
            return this;
        }

        @Override
        public HMMBuilder withInitialStateDistribution(ProbabilityMassFunction<State> initialDistribution) {
            basicBuilder.withInitialStateDistribution(initialDistribution);
            return this;
        }
    }

    public class MDPBuilder implements BasicMarkovianBuilderTemplate<MDPBuilder, A>,
            DecisionBasedMarkovionBuilderTemplate<MDPBuilder, A, Aa, R>, Builder<Markovian<A, R>> {

        @Override
        public Markovian<A, R> build() {
            decisionBuilder.decorates(basicBuilder.build());
            return decisionBuilder.build();
        }

        @Override
        public MDPBuilder calculateRewardWith(RewardReceiver<A, R> rewardCalc) {
            decisionBuilder.calculateRewardWith(rewardCalc);
            return this;
        }

        @Override
        public MDPBuilder withActionSpace(Set<Aa> actions) {
            decisionBuilder.withActionSpace(actions);
            return this;
        }

        @Override
        public MDPBuilder selectActionsAccordingTo(Policy<A, Aa> policy) {
            decisionBuilder.selectActionsAccordingTo(policy);
            return this;
        }

        @Override
        public MDPBuilder createStateSpaceNavigator(StateSpaceNavigator<A> stateSpaceNavigator) {
            basicBuilder.createStateSpaceNavigator(stateSpaceNavigator);
            return this;
        }

        @Override
        public MDPBuilder withInitialStateDistribution(ProbabilityMassFunction<State> initialDistribution) {
            basicBuilder.withInitialStateDistribution(initialDistribution);
            return this;
        }
    }

    public class POMDPBuilder implements BasicMarkovianBuilderTemplate<POMDPBuilder, A>,
            DecisionBasedMarkovionBuilderTemplate<POMDPBuilder, A, Aa, R>,
            HiddenStateMarkovianBuilderTemplate<POMDPBuilder>, Builder<Markovian<A, R>> {

        @Override
        public Markovian<A, R> build() {
            decisionBuilder.decorates(basicBuilder.build());
            hiddenBuilder.decorates(decisionBuilder.build());
            return hiddenBuilder.build();
        }

        @Override
        public POMDPBuilder handleObservationsWith(ObservationProducer obsHandler) {
            hiddenBuilder.handleObservationsWith(obsHandler);
            return this;
        }

        @Override
        public POMDPBuilder calculateRewardWith(RewardReceiver<A, R> rewardCalc) {
            decisionBuilder.calculateRewardWith(rewardCalc);
            return this;
        }

        @Override
        public POMDPBuilder withActionSpace(Set<Aa> actions) {
            decisionBuilder.withActionSpace(actions);
            return this;
        }

        @Override
        public POMDPBuilder selectActionsAccordingTo(Policy<A, Aa> policy) {
            decisionBuilder.selectActionsAccordingTo(policy);
            return this;
        }

        @Override
        public POMDPBuilder createStateSpaceNavigator(StateSpaceNavigator<A> stateSpaceNavigator) {
            basicBuilder.createStateSpaceNavigator(stateSpaceNavigator);
            return this;
        }

        @Override
        public POMDPBuilder withInitialStateDistribution(ProbabilityMassFunction<State> initialDistribution) {
            basicBuilder.withInitialStateDistribution(initialDistribution);
            return this;
        }
    }

    private BasicMarkovianBuilder<A, R> basicBuilder;
    private HiddenStateMarkovianBuilder<A, R> hiddenBuilder;
    private DecisionBasedMarkovianBuilder<A, Aa, R> decisionBuilder;

    private MarkovianBuilder() {
        this.basicBuilder = BasicMarkovianBuilder.createBasicMarkovian();
        this.hiddenBuilder = HiddenStateMarkovianBuilder.createHiddenStateMarkovianBuilder();
        this.decisionBuilder = DecisionBasedMarkovianBuilder.createDecisionBasedMarkovianBuilder();
    }

    private static <A, Aa extends Action<A>, R> MarkovianBuilder<A, Aa, R> createBuilder() {
        return new MarkovianBuilder<>();
    }

    public static <A, Aa extends Action<A>, R> MarkovianBuilder<A, Aa, R>.MarkovChainBuilder createMarkovChain() {
        return MarkovianBuilder.<A, Aa, R> createBuilder().new MarkovChainBuilder();
    }

    public static <A, Aa extends Action<A>, R> MarkovianBuilder<A, Aa, R>.HMMBuilder createHiddenMarkovModel() {
        return MarkovianBuilder.<A, Aa, R> createBuilder().new HMMBuilder();
    }

    public static <A, Aa extends Action<A>, R> MarkovianBuilder<A, Aa, R>.MDPBuilder createMarkovDecisionProcess() {
        return MarkovianBuilder.<A, Aa, R> createBuilder().new MDPBuilder();
    }

    public static <A, Aa extends Action<A>, R> MarkovianBuilder<A, Aa, R>.POMDPBuilder createPartiallyObservableMDP() {
        return MarkovianBuilder.<A, Aa, R> createBuilder().new POMDPBuilder();
    }
}
