package org.palladiosimulator.simexp.core.state;

import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.StateImpl;

public abstract class SelfAdaptiveSystemState<C, A, V> extends StateImpl {

    protected final SimulationRunnerHolder simulationRunnerHolder;

    protected PerceivableEnvironmentalState<V> perceivedState;
    protected ArchitecturalConfiguration<C, A> archConfiguration;
    protected StateQuantity quantifiedState;

    public SelfAdaptiveSystemState(SimulationRunnerHolder simulationRunnerHolder) {
        this.simulationRunnerHolder = simulationRunnerHolder;
    }

    public StateQuantity getQuantifiedState() {
        return quantifiedState;
    }

    public ArchitecturalConfiguration<C, A> getArchitecturalConfiguration() {
        return archConfiguration;
    }

    public PerceivableEnvironmentalState<V> getPerceivedEnvironmentalState() {
        return perceivedState;
    }

    @Override
    public String toString() {
        return String.format("%1s_%2s", perceivedState.getStringRepresentation(), archConfiguration.toString());
    }

    public void determineQuantifiedState() {
        simulationRunnerHolder.simulate(this);
    }

    public abstract SelfAdaptiveSystemState<C, A, V> transitToNext(PerceivableEnvironmentalState<V> perceivedState,
            ArchitecturalConfiguration<C, A> archConf);
}
