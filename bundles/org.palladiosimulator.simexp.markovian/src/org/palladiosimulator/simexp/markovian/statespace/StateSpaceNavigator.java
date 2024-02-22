package org.palladiosimulator.simexp.markovian.statespace;

import java.util.Optional;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public abstract class StateSpaceNavigator<A> {

    protected static final Logger LOGGER = Logger.getLogger(StateSpaceNavigator.class);

    public static class NavigationContext<A> {

        private final State source;
        private final Optional<Action<A>> action;

        private NavigationContext(State source, Action<A> action) {
            this.source = source;
            this.action = Optional.ofNullable(action);
        }

        public static <A, R> NavigationContext<A> of(Sample<A, R> sample) {
            return new NavigationContext<>(sample.getCurrent(), sample.getAction());
        }

        public Optional<Action<A>> getAction() {
            return action;
        }

        public State getSource() {
            return source;
        }

    }

    public abstract State navigate(NavigationContext<A> context);

}
