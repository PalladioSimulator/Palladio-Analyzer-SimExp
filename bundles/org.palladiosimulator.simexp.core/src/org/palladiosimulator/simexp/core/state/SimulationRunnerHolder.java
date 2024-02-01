package org.palladiosimulator.simexp.core.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;

public class SimulationRunnerHolder<S> {
    private final List<ExperienceSimulationRunner<S>> simulationRunner = new ArrayList<>();

    public void registerSimulationRunners(Collection<ExperienceSimulationRunner<S>> newSimulationRunners) {
        simulationRunner.addAll(newSimulationRunners);
    }

    public <A, V> void simulate(SelfAdaptiveSystemState<S, A, V> sasState) {
        if (simulationRunner.isEmpty()) {
            // TODO exception handling
            throw new RuntimeException("There has to be at one simulation runner.");
        }
        simulationRunner.forEach(runner -> runner.simulate(sasState));
    }
}
