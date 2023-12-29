package org.palladiosimulator.simexp.core.statespace;

import java.util.Optional;

import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.state.ArchitecturalConfiguration;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;

public class EnvironmentDrivenStateSpaceNavigator<S, A> extends SelfAdaptiveSystemStateSpaceNavigator<S, A> {

    private EnvironmentDrivenStateSpaceNavigator(EnvironmentProcess environmentalDynamics) {
        super(environmentalDynamics);
    }

    public static <S, A> EnvironmentDrivenStateSpaceNavigator<S, A> with(EnvironmentProcess environmentProcess) {
        return new EnvironmentDrivenStateSpaceNavigator<>(environmentProcess);
    }

    @Override
    public SelfAdaptiveSystemState<S> determineStructuralState(NavigationContext<S, A> context) {
        Optional<Action<A>> action = context.getAction();
        Reconfiguration<S> reconf = (Reconfiguration<S>) action.get();
        PerceivableEnvironmentalState nextEnvState = environmentalDynamics
            .determineNextGiven(getLastEnvironmentalState(context));
        ArchitecturalConfiguration<S> nextArchConf = getLastArchitecturalConfig(context).apply(reconf);
        LOGGER.info("==== End MAPE-K loop ====");
        SelfAdaptiveSystemState<S> nextState = getSasState(context).transitToNext(nextEnvState, nextArchConf);
        LOGGER.info(String.format("Transitioned to next state '%s'", nextState.toString()));
        return nextState;
    }

    private PerceivableEnvironmentalState getLastEnvironmentalState(NavigationContext<S, A> context) {
        return getSasState(context).getPerceivedEnvironmentalState();
    }

    private ArchitecturalConfiguration<S> getLastArchitecturalConfig(NavigationContext<S, A> context) {
        return getSasState(context).getArchitecturalConfiguration();
    }

    private SelfAdaptiveSystemState<S> getSasState(NavigationContext<S, A> context) {
        return (SelfAdaptiveSystemState<S>) context.getSource();
    }

    @Override
    protected PerceivableEnvironmentalState determineInitial(ArchitecturalConfiguration<S> initialArch) {
        return environmentalDynamics.determineInitial();
    }

}
