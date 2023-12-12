package org.palladiosimulator.simexp.core.state;

import java.util.List;

import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.StateImpl;

import com.google.common.collect.Lists;

public abstract class SelfAdaptiveSystemState<T> extends StateImpl<T> {

    private static List<ExperienceSimulationRunner> simulationRunner = Lists.newArrayList();

    protected PerceivableEnvironmentalState perceivedState;
    protected ArchitecturalConfiguration<T> archConfiguration;
    protected StateQuantity quantifiedState;

    public StateQuantity getQuantifiedState() {
        return quantifiedState;
    }

    public static void registerSimulationRunner(ExperienceSimulationRunner newSimulationRunner) {
        simulationRunner.add(newSimulationRunner);
    }

    public static List<ExperienceSimulationRunner> getSimulationRunner() {
        if (simulationRunner.isEmpty()) {
            // TODO exception handling
            throw new RuntimeException("There has to be at one simulation runner.");
        }
        return simulationRunner;
    }

    public ArchitecturalConfiguration<T> getArchitecturalConfiguration() {
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
        getSimulationRunner().forEach(runner -> runner.simulate(this));
    }

    public abstract SelfAdaptiveSystemState<?> transitToNext(PerceivableEnvironmentalState perceivedState,
            ArchitecturalConfiguration<?> archConf);

}
