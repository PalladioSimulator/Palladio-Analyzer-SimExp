package org.palladiosimulator.simexp.core.state;

import java.util.Optional;

import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.statespace.InductiveStateSpaceNavigator;

public abstract class ArchitecturalSpaceNavigator<C, A, V> extends InductiveStateSpaceNavigator<A> {

    @Override
    public State navigate(NavigationContext<A> context) {
        if (isValid(context)) {
            SelfAdaptiveSystemState<C, A, V> state = (SelfAdaptiveSystemState<C, A, V>) context.getSource();
            Optional<Action<A>> action = context.getAction();
            Reconfiguration<A> reconfiguration = (Reconfiguration<A>) action.get();
            return navigate(state.getArchitecturalConfiguration(), reconfiguration);
        }

        // TODO exception handling
        throw new RuntimeException("");
    }

    private boolean isValid(NavigationContext<A> context) {
        Optional<Action<A>> action = context.getAction();
        if (!action.isPresent()) {
            return false;
        }
        Action<A> actionValue = action.get();
        if (!(actionValue instanceof Reconfiguration<A>)) {
            return false;
        }
        State source = context.getSource();
        return source instanceof SelfAdaptiveSystemState;
    }

    public abstract State navigate(ArchitecturalConfiguration<C, A> archConfig, Reconfiguration<A> reconfiguration);

}
