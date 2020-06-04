package de.fzi.srp.core.model.factory;

import de.fzi.srp.core.model.markovmodel.markoventity.MarkovEntityFactory;
import de.fzi.srp.core.model.markovmodel.markoventity.Observation;
import de.fzi.srp.core.model.markovmodel.markoventity.Reward;
import de.fzi.srp.core.model.markovmodel.markoventity.State;
import de.fzi.srp.core.model.markovmodel.markoventity.Transition;

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
