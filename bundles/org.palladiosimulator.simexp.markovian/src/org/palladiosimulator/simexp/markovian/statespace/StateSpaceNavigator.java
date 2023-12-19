package org.palladiosimulator.simexp.markovian.statespace;

import java.util.Optional;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public abstract class StateSpaceNavigator<T> {

    protected static final Logger LOGGER = Logger.getLogger(StateSpaceNavigator.class);

    public static class NavigationContext<T> {

        private final State<T> source;
        private final Optional<Action<T>> action;

        private NavigationContext(State<T> source, Action<T> action) {
            this.source = source;
            this.action = Optional.ofNullable(action);
        }

        public static <T> NavigationContext<T> of(Sample<T> sample) {
            return new NavigationContext<>(sample.getCurrent(), sample.getAction());
        }

        public Optional<Action<T>> getAction() {
            return action;
        }

        public State<T> getSource() {
            return source;
        }

    }

    public abstract State<T> navigate(NavigationContext<T> context);

}
