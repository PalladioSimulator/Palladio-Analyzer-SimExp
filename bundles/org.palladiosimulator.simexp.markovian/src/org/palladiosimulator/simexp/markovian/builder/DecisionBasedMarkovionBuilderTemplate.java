package org.palladiosimulator.simexp.markovian.builder;

import java.util.Set;

import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.activity.RewardReceiver;
import org.palladiosimulator.simexp.markovian.builder.MarkovianBuilder.MDPBuilder;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;

public interface DecisionBasedMarkovionBuilderTemplate<T> {
	
	public MDPBuilder<T> calculateRewardWith(RewardReceiver rewardCalc);
	
	public MDPBuilder<T> withActionSpace(Set<Action<?>> actions);

	public MDPBuilder<T> selectActionsAccordingTo(Policy<Action<?>> policy);
}
