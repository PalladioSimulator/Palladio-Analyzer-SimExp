package org.palladiosimulator.simexp.core.simulation;

import java.util.List;

import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator.QualityMeasurements;

public interface ISimulationResult {
    double getTotalReward();

    /**
     * @return for every run a list entry with the quality attributes of the simulations
     */
    QualityMeasurements getQualityMeasurements();

    String getRewardDescription();

    List<String> getDetailDescription();
}
