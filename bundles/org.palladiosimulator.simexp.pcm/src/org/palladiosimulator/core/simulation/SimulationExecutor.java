package org.palladiosimulator.core.simulation;

public interface SimulationExecutor {
    String getPolicyId();

    void execute();

    void evaluate();

    void dispose();
}
