package de.fzi.srp.core.process.builder;

import java.util.Set;

import de.fzi.srp.core.model.markovmodel.markoventity.Action;
import de.fzi.srp.core.process.markovian.activity.Policy;
import de.fzi.srp.core.process.markovian.activity.RewardReceiver;

public interface DecisionBasedMarkovionBuilderTemplate<T> {
	
	public T calculateRewardWith(RewardReceiver rewardCalc);
	
	public T withActionSpace(Set<Action<?>> actions);

	public T selectActionsAccordingTo(Policy<Action<?>> policy);
}
