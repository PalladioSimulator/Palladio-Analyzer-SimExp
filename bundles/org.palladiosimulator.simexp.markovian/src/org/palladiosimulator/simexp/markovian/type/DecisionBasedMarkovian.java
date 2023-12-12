package org.palladiosimulator.simexp.markovian.type;

import java.util.Set;

import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.activity.RewardReceiver;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public class DecisionBasedMarkovian <T> extends MarkovianDecorator<T> {

	//private final Policy<Action<?>> policy;
	private final Policy<Action<T>> policy;
	private final RewardReceiver<T> rewardReceiver;
	private final Set<Action<T>> actionSpace;
	
	public DecisionBasedMarkovian(Markovian<T> decoratedMarkovian, 
								  Policy<Action<T>> policy, 
								  RewardReceiver<T> rewardReceiver,
								  Set<Action<T>> actionSpace) {
		super(decoratedMarkovian);
		this.policy = policy;
		this.rewardReceiver = rewardReceiver;
		this.actionSpace = actionSpace;
	}

	@Override
	public void drawSample(Sample<T> sample) {
		addSelectedAction(sample);
		addNextState(sample);
		addObtainedReward(sample);
	}

	private void addSelectedAction(Sample<T> sample) {
		// public T select(State<T> source, Set<T> options);
		State<T> current = sample.getCurrent();
        T choice = policy.select(current, actionSpace);
		// void setAction(Action<T> value);
		//Action<Action<T>> choice2 = null;
		sample.setAction(choice);
	}

	private void addNextState(Sample<T> sample) {
		decoratedMarkovian.drawSample(sample);
	}
	
	private void addObtainedReward(Sample<T> sample) {
		Reward<T> reward = rewardReceiver.obtain(sample);
		sample.setReward(reward);
	}

}
