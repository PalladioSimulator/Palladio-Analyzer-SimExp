package org.palladiosimulator.simexp.core.state;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;

public class RestoredSelfAdaptiveSystemState<C, A, V> extends SelfAdaptiveSystemState<C, A, V> {

    private final SpecialCaseStateQuantity quantifiedState;
    private final SelfAdaptiveSystemState<C, A, V> restoredState;

    private RestoredSelfAdaptiveSystemState(SimulationRunnerHolder simulationRunnerHolder,
            SelfAdaptiveSystemState<C, A, V> restoredState, SimulatedExperience experience) {
        super(simulationRunnerHolder);
        this.restoredState = restoredState;
        this.quantifiedState = new SpecialCaseStateQuantity(experience.getQuantifiedStateOfCurrent());
    }

    public static <S, A, V> RestoredSelfAdaptiveSystemState<S, A, V> restoreFrom(
            SimulationRunnerHolder simulationRunnerHolder, SimulatedExperience experience,
            SelfAdaptiveSystemState<S, A, V> restoredState) {
        return new RestoredSelfAdaptiveSystemState<>(simulationRunnerHolder, restoredState, experience);
    }

    @Override
    public StateQuantity getQuantifiedState() {
        return quantifiedState;
    }

    @Override
    public ArchitecturalConfiguration<C, A> getArchitecturalConfiguration() {
        return restoredState.getArchitecturalConfiguration();
    }

    @Override
    public PerceivableEnvironmentalState<V> getPerceivedEnvironmentalState() {
        return restoredState.getPerceivedEnvironmentalState();
    }

    @Override
    public SelfAdaptiveSystemState<C, A, V> transitToNext(PerceivableEnvironmentalState<V> perceivedState,
            ArchitecturalConfiguration<C, A> archConf) {
        return restoredState.transitToNext(perceivedState, archConf);
    }

    @Override
    public String toString() {
        return restoredState.toString();
    }

}
