package org.palladiosimulator.simexp.core.action;

import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;

public class DefaultReconfigurationFilter implements ReconfigurationFilter {

	@Override
	public Set<Action<?>> filterApplicables(SelfAdaptiveSystemState<?> source, Set<Reconfiguration<?>> reconfigurationSpace) {
		return reconfigurationSpace.stream().map(each -> (Action<?>) each).collect(Collectors.toSet());
	}
	
}
