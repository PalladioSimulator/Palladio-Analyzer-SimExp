package org.palladiosimulator.simexp.app.console.simulation;

import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator.QualityMeasurements;

public class ConsoleSimulationResult {
    public final Double reward;
    public final String error;
    public final QualityMeasurements qualityMeasurements;

    public ConsoleSimulationResult(double reward, QualityMeasurements qualityMeasurements) {
        this(reward, qualityMeasurements, null);
    }

    public ConsoleSimulationResult(String error) {
        this(null, null, error);
    }

    ConsoleSimulationResult(Double reward, QualityMeasurements qualityMeasurements, String error) {
        this.reward = reward;
        this.qualityMeasurements = qualityMeasurements;
        this.error = error;
    }
}
