package org.palladiosimulator.simexp.core.state;

import java.util.List;

import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.StateImpl;

public abstract class SelfAdaptiveSystemState<S, A> extends StateImpl<S> {

    protected final SimulationRunnerHolder<S, A> simulationRunnerHolder;

    protected PerceivableEnvironmentalState perceivedState;
    protected ArchitecturalConfiguration<S, A> archConfiguration;
    protected StateQuantity quantifiedState;

    public SelfAdaptiveSystemState(SimulationRunnerHolder<S, A> simulationRunnerHolder) {
        this.simulationRunnerHolder = simulationRunnerHolder;
    }

    public StateQuantity getQuantifiedState() {
        return quantifiedState;
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
        List<ExperienceSimulationRunner<S, A>> simulationRunners = simulationRunnerHolder.getSimulationRunner();
        simulationRunners.forEach(runner -> runner.simulate(this));
    }

    public abstract SelfAdaptiveSystemState<S, A> transitToNext(PerceivableEnvironmentalState perceivedState,
            ArchitecturalConfiguration<S, A> archConf);

}
