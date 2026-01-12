package org.palladiosimulator.simexp.core.evaluation;

import org.palladiosimulator.simexp.commons.constants.model.RewardType;

public interface TotalRewardCalculation {
    RewardType getRewardType();

    String getName();

    double computeTotalReward();

}