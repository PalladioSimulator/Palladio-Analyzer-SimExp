package org.palladiosimulator.simexp.dsl.ea.api.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.palladiosimulator.simexp.dsl.smodel.api.IPrecisionProvider;

public class RewardFormater implements IRewardRounder, IRewardFormater {
    private final IPrecisionProvider precisionProvider;

    public RewardFormater(IPrecisionProvider precisionProvider) {
        this.precisionProvider = precisionProvider;
    }

    @Override
    public double round(double reward) {
        BigDecimal bd = BigDecimal.valueOf(reward);
        bd = bd.setScale(precisionProvider.getPlaces(), RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public String asString(double reward) {
        return String.format("%s", reward);
    }
}
