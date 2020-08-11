package org.palladiosimulator.simexp.core.statespace;

import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.state.ArchitecturalConfiguration;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;

public class EnvironmentDrivenStateSpaceNavigator extends SelfAdaptiveSystemStateSpaceNavigator  {
	
	private EnvironmentDrivenStateSpaceNavigator(EnvironmentProcess environmentalDynamics) {
		super(environmentalDynamics);
	}
	
	public static EnvironmentDrivenStateSpaceNavigator with(EnvironmentProcess environmentProcess) {
		return new EnvironmentDrivenStateSpaceNavigator(environmentProcess);
	}
	
	@Override
	public SelfAdaptiveSystemState<?> determineStructuralState(NavigationContext context) {
		Reconfiguration<?> reconf = (Reconfiguration<?>) context.getAction().get();
		PerceivableEnvironmentalState nextEnvState = environmentalDynamics.determineNextGiven(getLastEnvironmentalState(context));
		ArchitecturalConfiguration<?> nextArchConf = getLastArchitecturalConfig(context).apply(reconf);
		return getSasState(context).transitToNext(nextEnvState, nextArchConf);
	}

	private PerceivableEnvironmentalState getLastEnvironmentalState(NavigationContext context) {
		return getSasState(context).getPerceivedEnvironmentalState();
	}

	private ArchitecturalConfiguration<?> getLastArchitecturalConfig(NavigationContext context) {
		return getSasState(context).getArchitecturalConfiguration();
	}
	
	private SelfAdaptiveSystemState<?> getSasState(NavigationContext context) {
		return (SelfAdaptiveSystemState<?>) context.getSource();
	}

	@Override
	protected PerceivableEnvironmentalState determineInitial(ArchitecturalConfiguration<?> initialArch) {
		return environmentalDynamics.determineInitial();
	}

}
