package org.palladiosimulator.simexp.markovian.type;

import java.util.Set;

import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.activity.RewardReceiver;
import org.palladiosimulator.simexp.markovian.activity.StateQuantityMonitor;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public class DecisionBasedMarkovian<A, Aa extends Action<A>, R> extends MarkovianDecorator<A, R> {

    private final Policy<A, Aa> policy;
    private final RewardReceiver<A, R> rewardReceiver;
    private final StateQuantityMonitor stateQuantityMonitor;
    private final Set<Aa> actionSpace;

    public DecisionBasedMarkovian(Markovian<A, R> decoratedMarkovian, Policy<A, Aa> policy,
            RewardReceiver<A, R> rewardReceiver, StateQuantityMonitor stateQuantityMonitor, Set<Aa> actionSpace) {
        super(decoratedMarkovian);
        this.policy = policy;
        this.rewardReceiver = rewardReceiver;
        this.stateQuantityMonitor = stateQuantityMonitor;
        this.actionSpace = actionSpace;
    }

    @Override
    public Sample<A, R> determineInitialState() {
        Sample<A, R> initialState = super.determineInitialState();
        stateQuantityMonitor.monitor(initialState.getCurrent());
        return initialState;
    }

    @Override
    public void drawSample(Sample<A, R> sample) {
        addSelectedAction(sample);
        addNextState(sample);
        stateQuantityMonitor.monitor(sample.getNext());
        addObtainedReward(sample);
    }

    private void addSelectedAction(Sample<A, R> sample) {
        State current = sample.getCurrent();
        Aa choice = policy.select(current, actionSpace);
        sample.setAction(choice);
    }

    private void addNextState(Sample<A, R> sample) {
        decoratedMarkovian.drawSample(sample);
    }

    private void addObtainedReward(Sample<A, R> sample) {
        Reward<R> reward = rewardReceiver.obtain(sample);
        sample.setReward(reward);
    }

}
