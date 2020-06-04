package org.palladiosimulator.simexp.markovian.builder;

import java.util.Set;

import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.activity.RewardReceiver;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;

public interface DecisionBasedMarkovionBuilderTemplate<T> {
	
	public T calculateRewardWith(RewardReceiver rewardCalc);
	
	public T withActionSpace(Set<Action<?>> actions);

	public T selectActionsAccordingTo(Policy<Action<?>> policy);
}
