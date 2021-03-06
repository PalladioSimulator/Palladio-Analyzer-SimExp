package org.palladiosimulator.simexp.markovian.exploration;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

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
