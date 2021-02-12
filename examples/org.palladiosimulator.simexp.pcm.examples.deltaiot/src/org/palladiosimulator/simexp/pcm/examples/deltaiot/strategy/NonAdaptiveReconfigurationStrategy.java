package org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy;

import java.util.Set;

import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;

public class NonAdaptiveReconfigurationStrategy extends ReconfigurationStrategy {

	private final static SharedKnowledge EMPTY_KNOWLEDGE = new SharedKnowledge();
	
	@Override
	public String getId() {
		return "NonAdaptiveReconfigurationStrategy";
	}

	@Override
	protected SharedKnowledge monitor(State source, Set<Reconfiguration<?>> options) {
		return EMPTY_KNOWLEDGE;
	}

	@Override
	protected boolean analyse(SharedKnowledge knowledge) {
		return false;
	}

	@Override
	protected Reconfiguration<?> plan(SharedKnowledge knowledge) {
		// Nothing to plan
		return emptyReconfiguration();
	}

	@Override
	protected Reconfiguration<?> emptyReconfiguration() {
		return QVToReconfiguration.empty();
	}

	
}
