package org.palladiosimulator.simexp.app.console.simulation;

import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator.QualityMeasurements;

public class ConsoleSimulationResult {
    public final Double reward;
    public final String rewardType;
    public final String error;
    public final QualityMeasurements qualityMeasurements;

    public ConsoleSimulationResult(double reward, String rewardType, QualityMeasurements qualityMeasurements) {
        this(reward, rewardType, qualityMeasurements, null);
    }

    public ConsoleSimulationResult(String error) {
        this(null, null, null, error);
    }

    ConsoleSimulationResult(Double reward, String rewardType, QualityMeasurements qualityMeasurements, String error) {
        this.reward = reward;
        this.rewardType = rewardType;
        this.qualityMeasurements = qualityMeasurements;
        this.error = error;
    }
}
