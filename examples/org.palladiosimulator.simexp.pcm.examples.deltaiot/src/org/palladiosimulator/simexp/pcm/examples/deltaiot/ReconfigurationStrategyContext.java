package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import java.util.Set;

import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;

public interface ReconfigurationStrategyContext {
	
	public Set<Reconfiguration<?>> getReconfigurationSpace();
	
	public boolean isSelectionPolicy();
	
	public <T extends Reconfiguration<?>> ReconfigurationStrategy<T> getStrategy();
	
	public Policy<Action<?>> getSelectionPolicy();
	
	public default String getStrategyID() {
		return isSelectionPolicy() ? getSelectionPolicy().getId() : getStrategy().getId();
	}
}
