package de.fzi.srp.core.process.markovian.activity;

import java.util.Set;

import de.fzi.srp.core.model.markovmodel.markoventity.State;

public interface Policy<T> {

	public String getId();
	
	public T select(State source, Set<T> options);
}
