package org.palladiosimulator.simexp.markovian.model.builder;

import java.util.Set;

import org.palladiosimulator.simexp.markovian.model.factory.MarkovModelFactory;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityFactory;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition;

public class BasicMarkovModelBuilder<T, A, R> {

    public class TransitionBuilder {

        private Transition<T, A> transition;

        public TransitionBuilder(State<T> source, State<T> target) {
            this.transition = markovModelFactory.<T, A> createTransitionBetween(source, target);
        }

        public BasicMarkovModelBuilder<T, A, R> andProbability(double value) {
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

    private static BasicMarkovModelBuilder builderInstance = null;

    private final MarkovModelFactory markovModelFactory;

    private MarkovModel<T, A, R> markovModel;

    protected BasicMarkovModelBuilder() {
        this.markovModelFactory = MarkovModelFactory.get();
        this.markovModel = MarkovEntityFactory.eINSTANCE.<T, A, R> createMarkovModel();
    }

    public static <T, A, R> BasicMarkovModelBuilder<T, A, R> get() {
        if (builderInstance == null) {
            builderInstance = new BasicMarkovModelBuilder<>();
        }
        return builderInstance;
    }

    public BasicMarkovModelBuilder<T, A, R> withStateSpace(Set<State<T>> states) {
        markovModel.getStateSpace()
            .addAll(states);
        return this;
    }

    public TransitionBuilder andTransitionBetween(State<T> source, State<T> target) {
        return new TransitionBuilder(source, target);
    }

    public MarkovModel<T, A, R> build() {
        return markovModel;
    }
}
