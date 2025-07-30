package org.palladiosimulator.simexp.core.statespace;

import java.util.Optional;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.state.ArchitecturalConfiguration;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.state.SimulationRunnerHolder;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceAccessor;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;

public class EnvironmentDrivenStateSpaceNavigator<C, A, R, V>
        extends SelfAdaptiveSystemStateSpaceNavigator<C, A, R, V> {
    private final static Logger LOGGER = Logger.getLogger(EnvironmentDrivenStateSpaceNavigator.class);

    private EnvironmentDrivenStateSpaceNavigator(EnvironmentProcess<A, R, V> environmentalDynamics,
            SimulatedExperienceAccessor accessor, SimulationRunnerHolder simulationRunnerHolder) {
        super(environmentalDynamics, accessor, simulationRunnerHolder);
    }

    public static <C, A, R, V> EnvironmentDrivenStateSpaceNavigator<C, A, R, V> with(
            EnvironmentProcess<A, R, V> environmentProcess, SimulatedExperienceAccessor accessor,
            SimulationRunnerHolder simulationRunnerHolder) {
        return new EnvironmentDrivenStateSpaceNavigator<>(environmentProcess, accessor, simulationRunnerHolder);
    }

    @Override
    public SelfAdaptiveSystemState<C, A, V> determineStructuralState(NavigationContext<A> context) {
        Optional<Action<A>> action = context.getAction();
        Reconfiguration<A> reconf = (Reconfiguration<A>) action.get();
        PerceivableEnvironmentalState<V> nextEnvState = environmentalDynamics
            .determineNextGiven(getLastEnvironmentalState(context));
        ArchitecturalConfiguration<C, A> nextArchConf = getLastArchitecturalConfig(context).apply(reconf);
        LOGGER.info("==== End MAPE-K loop ====");
        SelfAdaptiveSystemState<C, A, V> nextState = getSasState(context).transitToNext(nextEnvState, nextArchConf);
        LOGGER.info(String.format("Transitioned to next state '%s'", nextState.toString()));
        return nextState;
    }

    private PerceivableEnvironmentalState<V> getLastEnvironmentalState(NavigationContext<A> context) {
        return getSasState(context).getPerceivedEnvironmentalState();
    }

    private ArchitecturalConfiguration<C, A> getLastArchitecturalConfig(NavigationContext<A> context) {
        return getSasState(context).getArchitecturalConfiguration();
    }

    private SelfAdaptiveSystemState<C, A, V> getSasState(NavigationContext<A> context) {
        return (SelfAdaptiveSystemState<C, A, V>) context.getSource();
    }

    @Override
    protected PerceivableEnvironmentalState<V> determineInitial(ArchitecturalConfiguration<C, A> initialArch) {
        return environmentalDynamics.determineInitial();
    }

}
