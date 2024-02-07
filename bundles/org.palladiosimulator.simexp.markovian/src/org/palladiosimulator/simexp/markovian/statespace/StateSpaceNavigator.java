package org.palladiosimulator.simexp.markovian.statespace;

import java.util.Optional;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public abstract class StateSpaceNavigator<S, A> {

    protected static final Logger LOGGER = Logger.getLogger(StateSpaceNavigator.class);

    public static class NavigationContext<S, A> {

        private final State<S> source;
        private final Optional<Action<A>> action;

        private NavigationContext(State<S> source, Action<A> action) {
            this.source = source;
            this.action = Optional.ofNullable(action);
        }

        public static <S, A, R> NavigationContext<S, A> of(Sample<S, A, R, S> sample) {
            return new NavigationContext<>(sample.getCurrent(), sample.getAction());
        }

        public Optional<Action<A>> getAction() {
            return action;
        }

        public State<S> getSource() {
            return source;
        }

    }

    public abstract State<S> navigate(NavigationContext<S, A> context);

}
