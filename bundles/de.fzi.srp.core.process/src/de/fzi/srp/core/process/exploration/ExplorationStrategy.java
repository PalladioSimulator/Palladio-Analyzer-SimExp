package de.fzi.srp.core.process.exploration;

import java.util.Set;

import de.fzi.srp.core.model.markovmodel.markoventity.State;
import de.fzi.srp.core.process.markovian.activity.Policy;

public interface ExplorationStrategy<T> extends Policy<T> {
	
	@Override
	default T select(State source, Set<T> options) {
		return select(options);
	}
	
	public T select(Set<T> options);
}
