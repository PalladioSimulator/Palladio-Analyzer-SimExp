package org.palladiosimulator.simexp.core.state;

import java.util.Optional;

import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.statespace.InductiveStateSpaceNavigator;

public abstract class ArchitecturalSpaceNavigator<S, A, V> extends InductiveStateSpaceNavigator<S, A> {

    @Override
    public State<S> navigate(NavigationContext<S, A> context) {
        if (isValid(context)) {
            SelfAdaptiveSystemState<S, A, V> state = (SelfAdaptiveSystemState<S, A, V>) context.getSource();
            Optional<Action<A>> action = context.getAction();
            Reconfiguration<A> reconfiguration = (Reconfiguration<A>) action.get();
            return navigate(state.getArchitecturalConfiguration(), reconfiguration);
        }

        // TODO exception handling
        throw new RuntimeException("");
    }

    private boolean isValid(NavigationContext<S, A> context) {
        Optional<Action<A>> action = context.getAction();
        if (!action.isPresent()) {
            return false;
        }
        Action<A> actionValue = action.get();
        if (!(actionValue instanceof Reconfiguration<A>)) {
            return false;
        }
        State<S> source = context.getSource();
        return source instanceof SelfAdaptiveSystemState;
    }

    public abstract State<S> navigate(ArchitecturalConfiguration<S, A> archConfig, Reconfiguration<A> reconfiguration);

}
