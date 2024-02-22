package org.palladiosimulator.simexp.core.reward;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.RewardImpl;

public class SimpleRewardSignal extends RewardImpl<Integer> {

    private final static int POSITIV_REWARD_SIGNAL = 1;
    private final static int NEGATIV_REWARD_SIGNAL = -1;

    private SimpleRewardSignal(int value) {
        super.setValue(value);
    }

    public static SimpleRewardSignal createPositivReward() {
        return new SimpleRewardSignal(POSITIV_REWARD_SIGNAL);
    }

    public static SimpleRewardSignal createNegativReward() {
        return new SimpleRewardSignal(NEGATIV_REWARD_SIGNAL);
    }

    @Override
    public String toString() {
        return Integer.toString(getValue());
    }

}
