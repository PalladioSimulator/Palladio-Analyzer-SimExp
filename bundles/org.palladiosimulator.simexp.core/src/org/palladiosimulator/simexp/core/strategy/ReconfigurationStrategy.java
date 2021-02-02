package org.palladiosimulator.simexp.core.strategy;

import java.util.Set;

import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

public abstract class ReconfigurationStrategy implements Policy<Reconfiguration<?>> {

	@Override
	public Reconfiguration<?> select(State source, Set<Reconfiguration<?>> options) {
		SharedKnowledge knowledge = monitor(source, options);
		if (analyse(knowledge)) {
			return plan(knowledge);
		}
		return emptyReconfiguration();
	}

	protected abstract SharedKnowledge monitor(State source, Set<Reconfiguration<?>> options);

	protected abstract boolean analyse(SharedKnowledge knowledge);

	protected abstract Reconfiguration<?> plan(SharedKnowledge knowledge);
	
	protected abstract Reconfiguration<?> emptyReconfiguration();
}
