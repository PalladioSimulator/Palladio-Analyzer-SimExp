package org.palladiosimulator.simexp.core.state;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;

public class SimulationRunnerHolder<C, A> {
    private final List<ExperienceSimulationRunner<C, A>> simulationRunner = new ArrayList<>();

    public void registerSimulationRunner(ExperienceSimulationRunner<C, A> newSimulationRunner) {
        simulationRunner.add(newSimulationRunner);
    }

    public List<ExperienceSimulationRunner<C, A>> getSimulationRunner() {
        if (simulationRunner.isEmpty()) {
            // TODO exception handling
            throw new RuntimeException("There has to be at one simulation runner.");
        }
        List<ExperienceSimulationRunner<C, A>> runner = new ArrayList<>(simulationRunner);
        return runner;
    }
}
