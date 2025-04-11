package org.palladiosimulator.simexp.app.console;

public class ConsoleSimulationResult {
    public final double reward;
    public final String error;

    public ConsoleSimulationResult(double reward) {
        this(reward, "");
    }

    public ConsoleSimulationResult(String error) {
        this(0.0, error);
    }

    ConsoleSimulationResult(double reward, String error) {
        this.reward = reward;
        this.error = error;
    }
}
