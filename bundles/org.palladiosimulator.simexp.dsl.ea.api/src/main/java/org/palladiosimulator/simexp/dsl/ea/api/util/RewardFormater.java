package org.palladiosimulator.simexp.dsl.ea.api.util;

import org.palladiosimulator.simexp.dsl.smodel.api.IPrecisionProvider;

public class RewardFormater implements IRewardRounder, IRewardFormater {
    private final IPrecisionProvider precisionProvider;

    public RewardFormater(IPrecisionProvider precisionProvider) {
        this.precisionProvider = precisionProvider;
    }

    @Override
    public double round(double reward) {
        double multiplicator = (long) (1.0 / precisionProvider.getPrecision());
        return Math.round(reward * multiplicator) / multiplicator;
    }

    @Override
    public String asString(double reward) {
        return String.format("%s", reward);
    }
}
