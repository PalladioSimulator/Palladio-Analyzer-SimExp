package org.palladiosimulator.simexp.core.simulation;

import java.util.List;

public interface ISimulationResult {
    double getTotalReward();

    String getRewardDescription();

    List<String> getDetailDescription();
}
