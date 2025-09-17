package org.palladiosimulator.simexp.dsl.ea.api.util;

public class RewardUtil implements IRewardFormater {
    private final double epsilon;

    public RewardUtil(double epsilon) {
        this.epsilon = epsilon;
    }

    @Override
    public double getPrecision() {
        return epsilon;
    }

    public double round(double reward) {
        double multiplicator = (long) (1.0 / epsilon);
        return Math.round(reward * multiplicator) / multiplicator;
    }

    @Override
    public String asString(double reward) {
        return String.format("%s", reward);
    }
}
