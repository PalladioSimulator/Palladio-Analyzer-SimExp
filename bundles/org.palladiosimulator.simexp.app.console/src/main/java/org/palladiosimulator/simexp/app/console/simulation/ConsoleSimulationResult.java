package org.palladiosimulator.simexp.app.console.simulation;

public class ConsoleSimulationResult {
    public final Double reward;
    public final String error;

    public ConsoleSimulationResult(double reward) {
        this(reward, null);
    }

    public ConsoleSimulationResult(String error) {
        this(null, error);
    }

    ConsoleSimulationResult(Double reward, String error) {
        this.reward = reward;
        this.error = error;
    }
}
