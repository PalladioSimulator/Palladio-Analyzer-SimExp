package org.palladiosimulator.simexp.markovian.model.factory;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityFactory;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Observation;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition;

public class MarkovModelFactory {

	private final MarkovEntityFactory entityFactory = MarkovEntityFactory.eINSTANCE;

	private MarkovModelFactory() {

	}

	public static MarkovModelFactory get() {
		return new MarkovModelFactory();
	}

	public State createState(String name) {
		State newState = entityFactory.createState();
		newState.setName(name);
		return newState;
	}

	public Transition createTransitionBetween(State source, State target) {
		Transition newTransition = entityFactory.createTransition();
		newTransition.setSource(source);
		newTransition.setTarget(target);
		return newTransition;
	}

	public Reward<?> createRewardSignal() {
		return entityFactory.createReward();
	}

	public <T> Reward<T> createRewardSignal(T value) {
		Reward<T> reward = entityFactory.createReward();
		reward.setValue(value);
		return reward;
	}

	public <O> Observation<O> createObservation(O value) {
		Observation<O> obs = entityFactory.createObservation();
		obs.setValue(value);
		return obs;
	}

}
