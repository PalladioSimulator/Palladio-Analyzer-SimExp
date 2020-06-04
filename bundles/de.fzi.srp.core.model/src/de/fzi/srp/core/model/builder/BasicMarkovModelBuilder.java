package de.fzi.srp.core.model.builder;

import java.util.Set;

import de.fzi.srp.core.model.factory.MarkovModelFactory;
import de.fzi.srp.core.model.markovmodel.markoventity.Action;
import de.fzi.srp.core.model.markovmodel.markoventity.MarkovEntityFactory;
import de.fzi.srp.core.model.markovmodel.markoventity.MarkovModel;
import de.fzi.srp.core.model.markovmodel.markoventity.State;
import de.fzi.srp.core.model.markovmodel.markoventity.Transition;

public class BasicMarkovModelBuilder {
	
	public class TransitionBuilder {
		
		private Transition transition;
		
		public TransitionBuilder(State source, State target) {
			this.transition = markovModelFactory.createTransitionBetween(source, target);
		}
		
		public BasicMarkovModelBuilder andProbability(double value) {
			transition.setProbability(value);
			markovModel.getTransitions().add(transition);
			return BasicMarkovModelBuilder.this;
		}
		
		public TransitionBuilder withOptionalLabel(Action<?> action) {
			transition.setLabel(action);
			return this;
		}
		
	}
	
	private static BasicMarkovModelBuilder builderInstance = null;
	
	private final MarkovModelFactory markovModelFactory;
	
	private MarkovModel markovModel;
	
	protected BasicMarkovModelBuilder() {
		this.markovModelFactory = MarkovModelFactory.get();
		this.markovModel = MarkovEntityFactory.eINSTANCE.createMarkovModel();
	}
	
	public static BasicMarkovModelBuilder get() {
		if (builderInstance == null) {
			builderInstance = new BasicMarkovModelBuilder();
		}
		return builderInstance;
	}
	
	public BasicMarkovModelBuilder withStateSpace(Set<State> states) {
		markovModel.getStateSpace().addAll(states);
		return this;
	}
	
	public TransitionBuilder andTransitionBetween(State source, State target) {
		return new TransitionBuilder(source, target);
	}
	
	public MarkovModel build() {
		return markovModel;
	}
}
