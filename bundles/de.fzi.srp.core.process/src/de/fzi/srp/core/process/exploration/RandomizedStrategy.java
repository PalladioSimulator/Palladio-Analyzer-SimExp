package de.fzi.srp.core.process.exploration;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import de.fzi.srp.core.model.markovmodel.markoventity.State;
import de.fzi.srp.core.process.markovian.activity.Policy;

public class RandomizedStrategy<T> implements Policy<T> {

	private final static String STRATEGY_ID = "RandomizedSelectionStrategy";
	
	private final Random random = new Random(System.currentTimeMillis());

	@Override
	public T select(State source, Set<T> options) {
		return selectRandomly(options);
	}
	
	private T selectRandomly(Set<T> values) {
		int randomizedIndex = random.nextInt(values.size());
		return new ArrayList<T>(values).get(randomizedIndex);
	}

	@Override
	public String getId() {
		return STRATEGY_ID;
	}
}
