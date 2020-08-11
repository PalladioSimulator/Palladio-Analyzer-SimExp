package org.palladiosimulator.simexp.core.action;

import java.util.Set;

import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;

public interface ReconfigurationFilter {

//	protected final Set<Reconfiguration<?>> reconfigurationSpace;
//	
//	public ReconfigurationFilter(Set<Reconfiguration<?>> reconfigurationSpace) {
//		this.reconfigurationSpace = reconfigurationSpace;
//	}

//	public Set<Transition> filterTransitions(State source) {
//		if (isNotApplicable(source)) {
//			//TODO Exception handling
//			throw new RuntimeException("");
//		}
//		return filterReconfigurations((SelfAdaptiveSystemState<?>) source).stream().map(each -> (Transition) each)
//																				   .collect(Collectors.toSet());
//	}
//	
//	private boolean isNotApplicable(State source) {
//		return (source instanceof SelfAdaptiveSystemState<?>) == false;
//	}

	public abstract Set<Action<?>> filterApplicables(SelfAdaptiveSystemState<?> source, Set<Reconfiguration<?>> reconfigurationSpace);
}
