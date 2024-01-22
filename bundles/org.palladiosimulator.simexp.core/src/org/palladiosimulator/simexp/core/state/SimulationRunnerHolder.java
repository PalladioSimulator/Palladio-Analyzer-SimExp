package org.palladiosimulator.simexp.core.state;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;

public class SimulationRunnerHolder<S, A> {
    private final List<ExperienceSimulationRunner<S, A>> simulationRunner = new ArrayList<>();

    public void registerSimulationRunner(ExperienceSimulationRunner<S, A> newSimulationRunner) {
        simulationRunner.add(newSimulationRunner);
    }

    public List<ExperienceSimulationRunner<S, A>> getSimulationRunner() {
        if (simulationRunner.isEmpty()) {
            // TODO exception handling
            throw new RuntimeException("There has to be at one simulation runner.");
        }
        List<ExperienceSimulationRunner<S, A>> runner = new ArrayList<>(simulationRunner);
        return runner;
    }
}
