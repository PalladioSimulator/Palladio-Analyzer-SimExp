package org.palladiosimulator.simexp.markovian.builder;

import java.util.Objects;
import java.util.Set;

import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.activity.RewardReceiver;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.type.BasicMarkovian;
import org.palladiosimulator.simexp.markovian.type.DecisionBasedMarkovian;

public class DecisionBasedMarkovianBuilder<S, A, Aa extends Action<A>, R>
        implements DecisionBasedMarkovionBuilderTemplate<DecisionBasedMarkovianBuilder<S, A, Aa, R>, S, A, Aa, R>,
        Builder<DecisionBasedMarkovian<S, A, Aa, R>> {

    private RewardReceiver<S, A, R> rewardCalc;
    private Set<Aa> actionSpace;
    private Policy<S, A, Aa> policy;
    private BasicMarkovian<S, A, R> basicMarkovian;

    private DecisionBasedMarkovianBuilder() {
    }

    public static <S, A, Aa extends Action<A>, R> DecisionBasedMarkovianBuilder<S, A, Aa, R> createDecisionBasedMarkovianBuilder() {
        return new DecisionBasedMarkovianBuilder<>();
    }

    @Override
    public DecisionBasedMarkovianBuilder<S, A, Aa, R> calculateRewardWith(RewardReceiver<S, A, R> reCalc) {
        this.rewardCalc = reCalc;
        return this;
    }

    @Override
    public DecisionBasedMarkovianBuilder<S, A, Aa, R> withActionSpace(Set<Aa> actions) {
        this.actionSpace = actions;
        return this;
    }

    @Override
    public DecisionBasedMarkovianBuilder<S, A, Aa, R> selectActionsAccordingTo(Policy<S, A, Aa> policy) {
        this.policy = policy;
        return this;
    }

    public DecisionBasedMarkovianBuilder<S, A, Aa, R> decorates(BasicMarkovian<S, A, R> basicMarkovian) {
        this.basicMarkovian = basicMarkovian;
        return this;
    }

    @Override
    public DecisionBasedMarkovian<S, A, Aa, R> build() {
        Objects.requireNonNull(policy, "");
        Objects.requireNonNull(rewardCalc, "");
        Objects.requireNonNull(actionSpace, "");

        DecisionBasedMarkovian<S, A, Aa, R> decisionBasedMarkovian = new DecisionBasedMarkovian<S, A, Aa, R>(
                basicMarkovian, policy, rewardCalc, actionSpace);
        return decisionBasedMarkovian;
    }
}
