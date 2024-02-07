package org.palladiosimulator.simexp.markovian.type;

import java.util.Set;

import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.activity.RewardReceiver;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public class DecisionBasedMarkovian<S, A, Aa extends Action<A>, R> extends MarkovianDecorator<S, A, R> {

    private final Policy<S, A, Aa> policy;
    private final RewardReceiver<S, A, R> rewardReceiver;
    private final Set<Aa> actionSpace;

    public DecisionBasedMarkovian(Markovian<S, A, R> decoratedMarkovian, Policy<S, A, Aa> policy,
            RewardReceiver<S, A, R> rewardReceiver, Set<Aa> actionSpace) {
        super(decoratedMarkovian);
        this.policy = policy;
        this.rewardReceiver = rewardReceiver;
        this.actionSpace = actionSpace;
    }

    @Override
    public void drawSample(Sample<S, A, R, State<S>> sample) {
        addSelectedAction(sample);
        addNextState(sample);
        addObtainedReward(sample);
    }

    private void addSelectedAction(Sample<S, A, R, State<S>> sample) {
        State<S> current = sample.getCurrent();
        Aa choice = policy.select(current, actionSpace);
        sample.setAction(choice);
    }

    private void addNextState(Sample<S, A, R, State<S>> sample) {
        decoratedMarkovian.drawSample(sample);
    }

    private void addObtainedReward(Sample<S, A, R, State<S>> sample) {
        Reward<R> reward = rewardReceiver.obtain(sample);
        sample.setReward(reward);
    }

}
