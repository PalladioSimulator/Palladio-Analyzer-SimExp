package org.palladiosimulator.simexp.markovian.model.builder;

import java.util.Set;

import org.palladiosimulator.simexp.markovian.model.factory.MarkovModelFactory;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityFactory;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition;

public class BasicMarkovModelBuilder<S, A, R> {

    public class TransitionBuilder {

        private Transition<S, A> transition;

        public TransitionBuilder(State<S> source, State<S> target) {
            this.transition = markovModelFactory.<S, A> createTransitionBetween(source, target);
        }

        public BasicMarkovModelBuilder<S, A, R> andProbability(double value) {
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
    private final MarkovModel<S, A, R> markovModel;

    public BasicMarkovModelBuilder() {
        this.markovModelFactory = new MarkovModelFactory();
        this.markovModel = MarkovEntityFactory.eINSTANCE.<S, A, R> createMarkovModel();
    }

    public BasicMarkovModelBuilder<S, A, R> withStateSpace(Set<State<S>> states) {
        markovModel.getStateSpace()
            .addAll(states);
        return this;
    }

    public TransitionBuilder andTransitionBetween(State<S> source, State<S> target) {
        return new TransitionBuilder(source, target);
    }

    public MarkovModel<S, A, R> build() {
        return markovModel;
    }
}
