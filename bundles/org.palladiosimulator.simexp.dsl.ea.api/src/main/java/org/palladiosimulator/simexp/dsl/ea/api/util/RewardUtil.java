package org.palladiosimulator.simexp.dsl.ea.api.util;

import org.palladiosimulator.simexp.dsl.smodel.api.PrecisionProvider;

public class RewardUtil extends PrecisionProvider implements IRewardFormater {

    public RewardUtil(int places) {
        super(places);
    }

    public double round(double reward) {
        double multiplicator = (long) (1.0 / getPrecision());
        return Math.round(reward * multiplicator) / multiplicator;
    }

    @Override
    public String asString(double reward) {
        return String.format("%s", reward);
    }
}
