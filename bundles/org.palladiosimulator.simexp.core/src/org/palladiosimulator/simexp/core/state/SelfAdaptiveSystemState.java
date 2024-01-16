package org.palladiosimulator.simexp.core.state;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.StateImpl;

public abstract class SelfAdaptiveSystemState<S, A> extends StateImpl<S> {

    // TODO: singleton
    private static List<ExperienceSimulationRunner<?, ?>> simulationRunner = new ArrayList<>();

    protected PerceivableEnvironmentalState perceivedState;
    protected ArchitecturalConfiguration<S, A> archConfiguration;
    protected StateQuantity quantifiedState;

    public StateQuantity getQuantifiedState() {
        return quantifiedState;
    }

    public static <S, A> void registerSimulationRunner(ExperienceSimulationRunner<S, A> newSimulationRunner) {
        simulationRunner.add(newSimulationRunner);
    }

    public static <S, A> List<ExperienceSimulationRunner<S, A>> getSimulationRunner() {
        if (simulationRunner.isEmpty()) {
            // TODO exception handling
            throw new RuntimeException("There has to be at one simulation runner.");
        }
        List<ExperienceSimulationRunner<S, A>> runner = new ArrayList<>();
        for (ExperienceSimulationRunner<?, ?> r : simulationRunner) {
            runner.add((ExperienceSimulationRunner<S, A>) r);
        }
        return runner;
    }

    public ArchitecturalConfiguration<S, A> getArchitecturalConfiguration() {
        return archConfiguration;
    }

    public PerceivableEnvironmentalState getPerceivedEnvironmentalState() {
        return perceivedState;
    }

    @Override
    public String toString() {
        return String.format("%1s_%2s", perceivedState.getStringRepresentation(), archConfiguration.toString());
    }

    public void determineQuantifiedState() {
        SelfAdaptiveSystemState.<S, A> getSimulationRunner()
            .forEach(runner -> runner.simulate(this));
    }

    public abstract SelfAdaptiveSystemState<S, A> transitToNext(PerceivableEnvironmentalState perceivedState,
            ArchitecturalConfiguration<S, A> archConf);

}
