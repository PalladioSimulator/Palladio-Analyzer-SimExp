package org.palladiosimulator.simexp.core.state;

import java.util.Optional;

import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.statespace.InductiveStateSpaceNavigator;

public abstract class ArchitecturalSpaceNavigator<S, A> extends InductiveStateSpaceNavigator<S, A> {

    @Override
    public State<S> navigate(NavigationContext<S, A> context) {
        if (isValid(context)) {
            SelfAdaptiveSystemState<S> state = (SelfAdaptiveSystemState<S>) context.getSource();
            Optional<Action<A>> action = context.getAction();
            Reconfiguration<S> reconfiguration = (Reconfiguration<S>) action.get();
            return navigate(state.getArchitecturalConfiguration(), reconfiguration);
        }

        // TODO exception handling
        throw new RuntimeException("");
    }

    private boolean isValid(NavigationContext<S, A> context) {
        return context.getAction()
            .isPresent()
                && context.getAction()
                    .get() instanceof Reconfiguration<?>
                && context.getSource() instanceof SelfAdaptiveSystemState<?>;
    }

    public abstract State<S> navigate(ArchitecturalConfiguration<S> archConfig, Reconfiguration<S> reconfiguration);

}
