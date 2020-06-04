package org.palladiosimulator.simexp.markovian.statespace;

import java.util.Optional;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public abstract class StateSpaceNavigator {
	
	public static class NavigationContext {
		
		private final State source;
		private final Optional<Action<?>> action;
		
		private NavigationContext(State source, Action<?> action) {
			this.source = source;
			this.action = Optional.ofNullable(action);
		}
		
		public static NavigationContext of(Sample sample) {
			return new NavigationContext(sample.getCurrent(), sample.getAction());
		}
		
		public Optional<Action<?>> getAction() {
			return action;
		}		

		public State getSource() {
			return source;
		}
		
	}

	public abstract State navigate(NavigationContext context);
	
}
