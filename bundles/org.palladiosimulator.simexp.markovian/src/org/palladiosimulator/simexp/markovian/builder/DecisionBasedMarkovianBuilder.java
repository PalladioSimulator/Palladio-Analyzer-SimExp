package org.palladiosimulator.simexp.markovian.builder;

import java.util.Objects;
import java.util.Set;

import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.activity.RewardReceiver;
import org.palladiosimulator.simexp.markovian.activity.StateQuantityMonitor;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.type.BasicMarkovian;
import org.palladiosimulator.simexp.markovian.type.DecisionBasedMarkovian;

public class DecisionBasedMarkovianBuilder<A, Aa extends Action<A>, R>
        implements DecisionBasedMarkovionBuilderTemplate<DecisionBasedMarkovianBuilder<A, Aa, R>, A, Aa, R>,
        Builder<DecisionBasedMarkovian<A, Aa, R>> {

    private RewardReceiver<A, R> rewardCalc;
    private StateQuantityMonitor stateQuantityMonitor;
    private Set<Aa> actionSpace;
    private Policy<A, Aa> policy;
    private BasicMarkovian<A, R> basicMarkovian;

    private DecisionBasedMarkovianBuilder() {
    }

    public static <A, Aa extends Action<A>, R> DecisionBasedMarkovianBuilder<A, Aa, R> createDecisionBasedMarkovianBuilder() {
        return new DecisionBasedMarkovianBuilder<>();
    }

    @Override
    public DecisionBasedMarkovianBuilder<A, Aa, R> calculateRewardWith(RewardReceiver<A, R> reCalc) {
        this.rewardCalc = reCalc;
        return this;
    }

    public DecisionBasedMarkovianBuilder<A, Aa, R> withStateQuantityMonitor(StateQuantityMonitor stateQuantityMonitor) {
        this.stateQuantityMonitor = stateQuantityMonitor;
        return this;
    }

    @Override
    public DecisionBasedMarkovianBuilder<A, Aa, R> withActionSpace(Set<Aa> actions) {
        this.actionSpace = actions;
        return this;
    }

    @Override
    public DecisionBasedMarkovianBuilder<A, Aa, R> selectActionsAccordingTo(Policy<A, Aa> policy) {
        this.policy = policy;
        return this;
    }

    public DecisionBasedMarkovianBuilder<A, Aa, R> decorates(BasicMarkovian<A, R> basicMarkovian) {
        this.basicMarkovian = basicMarkovian;
        return this;
    }

    @Override
    public DecisionBasedMarkovian<A, Aa, R> build() {
        Objects.requireNonNull(policy, "");
        Objects.requireNonNull(rewardCalc, "");
        Objects.requireNonNull(actionSpace, "");

        DecisionBasedMarkovian<A, Aa, R> decisionBasedMarkovian = new DecisionBasedMarkovian<>(basicMarkovian,
                policy, rewardCalc, stateQuantityMonitor, actionSpace);
        return decisionBasedMarkovian;
    }
}
