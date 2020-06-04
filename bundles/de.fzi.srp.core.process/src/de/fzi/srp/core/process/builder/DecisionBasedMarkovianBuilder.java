package de.fzi.srp.core.process.builder;

import java.util.Objects;
import java.util.Set;

import de.fzi.srp.core.model.markovmodel.markoventity.Action;
import de.fzi.srp.core.process.markovian.BasicMarkovian;
import de.fzi.srp.core.process.markovian.DecisionBasedMarkovian;
import de.fzi.srp.core.process.markovian.activity.Policy;
import de.fzi.srp.core.process.markovian.activity.RewardReceiver;

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
