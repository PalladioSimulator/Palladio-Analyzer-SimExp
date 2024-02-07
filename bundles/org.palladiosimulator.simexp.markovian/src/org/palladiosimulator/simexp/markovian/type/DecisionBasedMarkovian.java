package org.palladiosimulator.simexp.markovian.type;

import java.util.Set;

import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.activity.RewardReceiver;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public class DecisionBasedMarkovian<S, A, Aa extends Action<A>, R, O> extends MarkovianDecorator<S, A, R, O> {

    // private final Policy<Action<?>> policy;
    // private final RewardReceiver rewardReceiver;
    // private final Set<Action<?>> actionSpace;
    private final Policy<S, A, Aa> policy;
    private final RewardReceiver<S, A, R, O> rewardReceiver;
    private final Set<Aa> actionSpace;

    public DecisionBasedMarkovian(Markovian<S, A, R, O> decoratedMarkovian, Policy<S, A, Aa> policy,
            RewardReceiver<S, A, R, O> rewardReceiver, Set<Aa> actionSpace) {
        super(decoratedMarkovian);
        this.policy = policy;
        this.rewardReceiver = rewardReceiver;
        this.actionSpace = actionSpace;
    }

    @Override
    public void drawSample(Sample<S, A, R, O> sample) {
        addSelectedAction(sample);
        addNextState(sample);
        addObtainedReward(sample);
    }

    private void addSelectedAction(Sample<S, A, R, O> sample) {
        // private void addSelectedAction(Sample sample) {
        // Action<?> choice = policy.select(sample.getCurrent(), actionSpace);
        // sample.setAction(choice);
        State<S> current = sample.getCurrent();
        Aa choice = policy.select(current, actionSpace);
        sample.setAction(choice);
    }

    private void addNextState(Sample<S, A, R, O> sample) {
        decoratedMarkovian.drawSample(sample);
    }

    private void addObtainedReward(Sample<S, A, R, O> sample) {
        Reward<R> reward = rewardReceiver.obtain(sample);
        sample.setReward(reward);
    }

}
