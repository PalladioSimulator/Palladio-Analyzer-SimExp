package org.palladiosimulator.simexp.markovian.builder;

import java.util.Objects;
import java.util.Set;

import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.activity.RewardReceiver;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.type.BasicMarkovian;
import org.palladiosimulator.simexp.markovian.type.DecisionBasedMarkovian;

public class DecisionBasedMarkovianBuilder implements DecisionBasedMarkovionBuilderTemplate<DecisionBasedMarkovianBuilder>, Builder<DecisionBasedMarkovian> {

	private RewardReceiver rewardCalc;
	private Set<Action<?>> actionSpace;
	private Policy<Action<?>> policy;
	private BasicMarkovian basicMarkovian;
	
	private DecisionBasedMarkovianBuilder() {
		
	}
	
	public static DecisionBasedMarkovianBuilder createDecisionBasedMarkovianBuilder() {
		return new DecisionBasedMarkovianBuilder();
	}
	
	@Override
	public DecisionBasedMarkovianBuilder calculateRewardWith(RewardReceiver reCalc) {
		this.rewardCalc = reCalc;
		return this;
	}
	
	@Override
	public DecisionBasedMarkovianBuilder withActionSpace(Set<Action<?>> actions) {
		this.actionSpace = actions;
		return this;
	}

	@Override
	public DecisionBasedMarkovianBuilder selectActionsAccordingTo(Policy<Action<?>> policy) {
		this.policy = policy;
		return this;
	}
	
	public DecisionBasedMarkovianBuilder decorates(BasicMarkovian basicMarkovian) {
		this.basicMarkovian = basicMarkovian;
		return this;
	}
	
	@Override
	public DecisionBasedMarkovian build() {
		Objects.requireNonNull(policy, "");
		Objects.requireNonNull(rewardCalc, "");
		Objects.requireNonNull(actionSpace, "");
		
		return new DecisionBasedMarkovian(basicMarkovian, policy, rewardCalc, actionSpace);
	}
}
