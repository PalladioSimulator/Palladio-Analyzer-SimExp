package de.fzi.srp.core.process.markovian;

import java.util.Set;

import de.fzi.srp.core.model.markovmodel.markoventity.Action;
import de.fzi.srp.core.model.markovmodel.markoventity.Reward;
import de.fzi.srp.core.model.markovmodel.samplemodel.Sample;
import de.fzi.srp.core.process.markovian.activity.Policy;
import de.fzi.srp.core.process.markovian.activity.RewardReceiver;

public class DecisionBasedMarkovian extends MarkovianDecorator {

	private final Policy<Action<?>> policy;
	private final RewardReceiver rewardReceiver;
	private final Set<Action<?>> actionSpace;
	
	public DecisionBasedMarkovian(Markovian decoratedMarkovian, 
								  Policy<Action<?>> policy, 
								  RewardReceiver rewardReceiver,
								  Set<Action<?>> actionSpace) {
		super(decoratedMarkovian);
		this.policy = policy;
		this.rewardReceiver = rewardReceiver;
		this.actionSpace = actionSpace;
	}

	@Override
	public void drawSample(Sample sample) {
		addSelectedAction(sample);
		addNextState(sample);
		addObtainedReward(sample);
	}

	private void addSelectedAction(Sample sample) {
		Action<?> choice = policy.select(sample.getCurrent(), actionSpace);
		sample.setAction(choice);
	}

	private void addNextState(Sample sample) {
		decoratedMarkovian.drawSample(sample);
	}
	
	private void addObtainedReward(Sample sample) {
		Reward<?> reward = rewardReceiver.obtain(sample);
		sample.setReward(reward);
	}

}
