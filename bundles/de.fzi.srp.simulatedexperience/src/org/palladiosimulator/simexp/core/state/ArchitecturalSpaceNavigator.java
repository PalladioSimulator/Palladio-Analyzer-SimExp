package org.palladiosimulator.simexp.core.state;

import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.statespace.InductiveStateSpaceNavigator;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator.NavigationContext;

public abstract class ArchitecturalSpaceNavigator extends InductiveStateSpaceNavigator {

	@Override
	public State navigate(NavigationContext context) {
		if (isValid(context)) {
			SelfAdaptiveSystemState<?> state = (SelfAdaptiveSystemState<?>) context.getSource();
			Reconfiguration<?> reconfiguration = (Reconfiguration<?>) context.getAction().get();
			return navigate(state.getArchitecturalConfiguration(), reconfiguration);
		}
		
		//TODO exception handling
		throw new RuntimeException("");
	}
	
	private boolean isValid(NavigationContext context) {
		return context.getAction().isPresent() &&
			   context.getAction().get() instanceof Reconfiguration<?> &&
			   context.getSource() instanceof SelfAdaptiveSystemState<?>;
	}
	
	public abstract State navigate(ArchitecturalConfiguration<?> archConfig, Reconfiguration<?> reconfiguration);

}
