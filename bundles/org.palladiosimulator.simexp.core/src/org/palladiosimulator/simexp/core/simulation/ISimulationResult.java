package org.palladiosimulator.simexp.core.simulation;

import java.util.List;
import java.util.Map;

public interface ISimulationResult {
    double getTotalReward();

    /**
     * @return for every run a list entry with the quality attributes of the simulations
     */
    List<Map<String, List<Double>>> getQualityAttributes();

    String getRewardDescription();

    List<String> getDetailDescription();
}
