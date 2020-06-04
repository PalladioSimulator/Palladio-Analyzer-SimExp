package org.palladiosimulator.simexp.markovian.model.builder;

import java.util.Set;

import org.palladiosimulator.simexp.markovian.model.factory.MarkovModelFactory;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityFactory;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition;

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
