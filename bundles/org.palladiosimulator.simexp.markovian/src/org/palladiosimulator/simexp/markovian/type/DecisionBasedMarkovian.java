package org.palladiosimulator.simexp.markovian.type;

import java.util.Set;

import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.activity.RewardReceiver;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public class DecisionBasedMarkovian<T, A extends Action<T>> extends MarkovianDecorator<T> {

    // private final Policy<Action<?>> policy;
    // private final RewardReceiver rewardReceiver;
    // private final Set<Action<?>> actionSpace;
    private final Policy<T> policy;
    private final RewardReceiver<T> rewardReceiver;
    private final Set<T> actionSpace;

    public DecisionBasedMarkovian(Markovian<T> decoratedMarkovian, Policy<Action<T>> policy,
            RewardReceiver<T> rewardReceiver, Set<Action<T>> actionSpace) {
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
        // private void addSelectedAction(Sample sample) {
        // Action<?> choice = policy.select(sample.getCurrent(), actionSpace);
        // sample.setAction(choice);

        // public T select(State<T> source, Set<T> options);
        State<T> current = sample.getCurrent();
        // State<PCMInstance>

        // Action<T> select(State<Action<T>> source, Set<Action<T>> options);

        // public T select(State<T> source, Set<T> options);
        T choice = policy.select(current, actionSpace);

        // QVToReconfiguration choice = null;
        // void setAction(Action<T> value);
        // Action<Action<T>> choice2 = null;
        // void setAction(Action<Action<T>> value);
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
