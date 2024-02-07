package org.palladiosimulator.simexp.markovian.builder;

import java.util.Set;

import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.activity.RewardReceiver;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;

public interface DecisionBasedMarkovionBuilderTemplate<T, A, Aa extends Action<A>, R> {

    public T calculateRewardWith(RewardReceiver<A, R> rewardCalc);

    public T withActionSpace(Set<Aa> actions);

    public T selectActionsAccordingTo(Policy<A, Aa> policy);
}
