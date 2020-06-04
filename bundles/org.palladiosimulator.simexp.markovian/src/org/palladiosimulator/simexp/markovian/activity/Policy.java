package org.palladiosimulator.simexp.markovian.activity;

import java.util.Set;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

public interface Policy<T> {

	public String getId();
	
	public T select(State source, Set<T> options);
}
