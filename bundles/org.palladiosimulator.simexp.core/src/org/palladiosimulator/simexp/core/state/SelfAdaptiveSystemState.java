package org.palladiosimulator.simexp.core.state;

import java.util.List;

import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.StateImpl;

public abstract class SelfAdaptiveSystemState<C, A> extends StateImpl {

    protected final SimulationRunnerHolder<C, A> simulationRunnerHolder;

    protected PerceivableEnvironmentalState perceivedState;
    protected ArchitecturalConfiguration<C, A> archConfiguration;
    protected StateQuantity quantifiedState;

    public SelfAdaptiveSystemState(SimulationRunnerHolder<C, A> simulationRunnerHolder) {
        this.simulationRunnerHolder = simulationRunnerHolder;
    }

    public StateQuantity getQuantifiedState() {
        return quantifiedState;
    }

    public ArchitecturalConfiguration<C, A> getArchitecturalConfiguration() {
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
        List<ExperienceSimulationRunner<C, A>> simulationRunners = simulationRunnerHolder.getSimulationRunner();
        simulationRunners.forEach(runner -> runner.simulate(this));
    }

    public abstract SelfAdaptiveSystemState<C, A> transitToNext(PerceivableEnvironmentalState perceivedState,
            ArchitecturalConfiguration<C, A> archConf);

}
