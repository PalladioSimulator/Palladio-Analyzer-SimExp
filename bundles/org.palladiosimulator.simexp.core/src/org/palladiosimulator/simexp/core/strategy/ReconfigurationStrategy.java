package org.palladiosimulator.simexp.core.strategy;

import java.util.Set;

import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

// This class is not yet integrated in the simulated experience process. This is rather a reminder for future work. 
// The idea is to introduce a dedicated concept for reconfiguration strategies following the MAPE-K principles.
public abstract class ReconfigurationStrategy<T> implements Policy<Reconfiguration<T>> {

	@Override
	public Reconfiguration<T> select(State source, Set<Reconfiguration<T>> options) {
		SharedKnowledge knowledge = monitor();
		if (analyse(knowledge)) {
			return plan(knowledge);
		}
		return emptyReconfiguration();
	}

	protected abstract SharedKnowledge monitor();

	protected abstract boolean analyse(SharedKnowledge knowledge);

	protected abstract Reconfiguration<T> plan(SharedKnowledge knowledge);
	
	protected abstract Reconfiguration<T> emptyReconfiguration();
}
