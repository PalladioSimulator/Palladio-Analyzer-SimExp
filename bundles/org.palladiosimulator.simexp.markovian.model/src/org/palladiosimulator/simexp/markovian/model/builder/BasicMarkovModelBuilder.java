package org.palladiosimulator.simexp.markovian.model.builder;

import java.util.Set;

import org.palladiosimulator.simexp.markovian.model.factory.MarkovModelFactory;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityFactory;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition;

public class BasicMarkovModelBuilder<A, R> {

    public class TransitionBuilder {

        private Transition<A> transition;

        public TransitionBuilder(State source, State target) {
            this.transition = markovModelFactory.<A> createTransitionBetween(source, target);
        }

        public BasicMarkovModelBuilder<A, R> andProbability(double value) {
            transition.setProbability(value);
            markovModel.getTransitions()
                .add(transition);
            return BasicMarkovModelBuilder.this;
        }

        public TransitionBuilder withOptionalLabel(Action<A> action) {
            transition.setLabel(action);
            return this;
        }

    }

    private final MarkovModelFactory markovModelFactory;
    private final MarkovModel<A, R> markovModel;

    public BasicMarkovModelBuilder() {
        this.markovModelFactory = new MarkovModelFactory();
        this.markovModel = MarkovEntityFactory.eINSTANCE.<A, R> createMarkovModel();
    }

    public BasicMarkovModelBuilder<A, R> withStateSpace(Set<State> states) {
        markovModel.getStateSpace()
            .addAll(states);
        return this;
    }

    public TransitionBuilder andTransitionBetween(State source, State target) {
        return new TransitionBuilder(source, target);
    }

    public MarkovModel<A, R> build() {
        return markovModel;
    }
}
