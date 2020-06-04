package org.palladiosimulator.simexp.markovian.type;

import java.util.Set;

import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.activity.RewardReceiver;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

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
