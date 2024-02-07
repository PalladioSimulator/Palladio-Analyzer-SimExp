package org.palladiosimulator.simexp.markovian.builder;

import java.util.Set;

import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.activity.RewardReceiver;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

public interface DecisionBasedMarkovionBuilderTemplate<T, S, A, Aa extends Action<A>, R> {

    public T calculateRewardWith(RewardReceiver<S, A, R, State<S>> rewardCalc);

    public T withActionSpace(Set<Aa> actions);

    public T selectActionsAccordingTo(Policy<S, A, Aa> policy);
}
