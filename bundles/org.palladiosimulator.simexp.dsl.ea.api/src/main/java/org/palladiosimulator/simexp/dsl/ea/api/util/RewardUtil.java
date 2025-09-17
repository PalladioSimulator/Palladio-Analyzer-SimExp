package org.palladiosimulator.simexp.dsl.ea.api.util;

public class RewardUtil {
    private final double epsilon;

    public RewardUtil(double epsilon) {
        this.epsilon = epsilon;
    }

    public double round(double number) {
        double multiplicator = (long) (1.0 / epsilon);
        return Math.round(number * multiplicator) / multiplicator;
    }
}
