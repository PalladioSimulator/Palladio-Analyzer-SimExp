package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import java.util.Set;

import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.NonAdaptiveReconfigurationStrategy;

import com.google.common.collect.Sets;

public class NonAdaptiveStrategyContext implements ReconfigurationStrategyContext {

	private final ReconfigurationStrategy nonAdaptiveStrategy = new NonAdaptiveReconfigurationStrategy();
	
	@Override
	public Set<Reconfiguration<?>> getReconfigurationSpace() {
		return Sets.newHashSet();
	}

	@Override
	public boolean isSelectionPolicy() {
		return false;
	}

	@Override
	public ReconfigurationStrategy getStrategy() {
		return nonAdaptiveStrategy;
	}

	@Override
	public Policy<Action<?>> getSelectionPolicy() {
		return null;
	}
	
	
}
