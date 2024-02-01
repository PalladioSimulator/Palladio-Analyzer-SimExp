package org.palladiosimulator.simexp.core.statespace;

import java.util.Optional;

import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.state.ArchitecturalConfiguration;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.state.SimulationRunnerHolder;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;

public class EnvironmentDrivenStateSpaceNavigator<S, A, R, V>
        extends SelfAdaptiveSystemStateSpaceNavigator<S, A, R, V> {

    private EnvironmentDrivenStateSpaceNavigator(EnvironmentProcess<S, A, R, V> environmentalDynamics,
            SimulatedExperienceStore<S, A, R> simulatedExperienceStore,
            SimulationRunnerHolder<S> simulationRunnerHolder) {
        super(environmentalDynamics, simulatedExperienceStore, simulationRunnerHolder);
    }

    public static <S, A, R, V> EnvironmentDrivenStateSpaceNavigator<S, A, R, V> with(
            EnvironmentProcess<S, A, R, V> environmentProcess,
            SimulatedExperienceStore<S, A, R> simulatedExperienceStore,
            SimulationRunnerHolder<S> simulationRunnerHolder) {
        return new EnvironmentDrivenStateSpaceNavigator<>(environmentProcess, simulatedExperienceStore,
                simulationRunnerHolder);
    }

    @Override
    public SelfAdaptiveSystemState<S, A, V> determineStructuralState(NavigationContext<S, A> context) {
        Optional<Action<A>> action = context.getAction();
        Reconfiguration<A> reconf = (Reconfiguration<A>) action.get();
        PerceivableEnvironmentalState<V> nextEnvState = environmentalDynamics
            .determineNextGiven(getLastEnvironmentalState(context));
        ArchitecturalConfiguration<S, A> nextArchConf = getLastArchitecturalConfig(context).apply(reconf);
        LOGGER.info("==== End MAPE-K loop ====");
        SelfAdaptiveSystemState<S, A, V> nextState = getSasState(context).transitToNext(nextEnvState, nextArchConf);
        LOGGER.info(String.format("Transitioned to next state '%s'", nextState.toString()));
        return nextState;
    }

    private PerceivableEnvironmentalState<V> getLastEnvironmentalState(NavigationContext<S, A> context) {
        return getSasState(context).getPerceivedEnvironmentalState();
    }

    private ArchitecturalConfiguration<S, A> getLastArchitecturalConfig(NavigationContext<S, A> context) {
        return getSasState(context).getArchitecturalConfiguration();
    }

    private SelfAdaptiveSystemState<S, A, V> getSasState(NavigationContext<S, A> context) {
        return (SelfAdaptiveSystemState<S, A, V>) context.getSource();
    }

    @Override
    protected PerceivableEnvironmentalState<V> determineInitial(ArchitecturalConfiguration<S, A> initialArch) {
        return environmentalDynamics.determineInitial();
    }

}
