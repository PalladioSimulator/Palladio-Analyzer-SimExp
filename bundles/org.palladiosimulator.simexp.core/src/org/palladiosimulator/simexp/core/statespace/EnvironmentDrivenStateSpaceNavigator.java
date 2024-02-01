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

public class EnvironmentDrivenStateSpaceNavigator<S, A, R> extends SelfAdaptiveSystemStateSpaceNavigator<S, A, R> {

    private EnvironmentDrivenStateSpaceNavigator(EnvironmentProcess<S, A, R> environmentalDynamics,
            SimulatedExperienceStore<S, A, R> simulatedExperienceStore,
            SimulationRunnerHolder<S> simulationRunnerHolder) {
        super(environmentalDynamics, simulatedExperienceStore, simulationRunnerHolder);
    }

    public static <S, A, R> EnvironmentDrivenStateSpaceNavigator<S, A, R> with(
            EnvironmentProcess<S, A, R> environmentProcess, SimulatedExperienceStore<S, A, R> simulatedExperienceStore,
            SimulationRunnerHolder<S> simulationRunnerHolder) {
        return new EnvironmentDrivenStateSpaceNavigator<>(environmentProcess, simulatedExperienceStore,
                simulationRunnerHolder);
    }

    @Override
    public SelfAdaptiveSystemState<S, A> determineStructuralState(NavigationContext<S, A> context) {
        Optional<Action<A>> action = context.getAction();
        Reconfiguration<A> reconf = (Reconfiguration<A>) action.get();
        PerceivableEnvironmentalState nextEnvState = environmentalDynamics
            .determineNextGiven(getLastEnvironmentalState(context));
        ArchitecturalConfiguration<S, A> nextArchConf = getLastArchitecturalConfig(context).apply(reconf);
        LOGGER.info("==== End MAPE-K loop ====");
        SelfAdaptiveSystemState<S, A> nextState = getSasState(context).transitToNext(nextEnvState, nextArchConf);
        LOGGER.info(String.format("Transitioned to next state '%s'", nextState.toString()));
        return nextState;
    }

    private PerceivableEnvironmentalState getLastEnvironmentalState(NavigationContext<S, A> context) {
        return getSasState(context).getPerceivedEnvironmentalState();
    }

    private ArchitecturalConfiguration<S, A> getLastArchitecturalConfig(NavigationContext<S, A> context) {
        return getSasState(context).getArchitecturalConfiguration();
    }

    private SelfAdaptiveSystemState<S, A> getSasState(NavigationContext<S, A> context) {
        return (SelfAdaptiveSystemState<S, A>) context.getSource();
    }

    @Override
    protected PerceivableEnvironmentalState determineInitial(ArchitecturalConfiguration<S, A> initialArch) {
        return environmentalDynamics.determineInitial();
    }

}
