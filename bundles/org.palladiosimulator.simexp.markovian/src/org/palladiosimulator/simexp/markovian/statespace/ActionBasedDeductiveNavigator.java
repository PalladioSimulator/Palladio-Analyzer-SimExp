package org.palladiosimulator.simexp.markovian.statespace;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition;

public class ActionBasedDeductiveNavigator<S, A, R> extends DeductiveStateSpaceNavigator<S, A, R> {

    public ActionBasedDeductiveNavigator(MarkovModel<S, A, R> markovModel) {
        super(markovModel);
    }

    @Override
    public State<S> navigate(NavigationContext<S, A> context) {
        // TODO exception handling
        if (context.getAction()
            .isPresent() == false) {
            throw new RuntimeException("");
        }

        Transition<S, A> result = markovModelAccessor.findTransition(context.getSource(), context.getAction()
            .get())
            .orElseThrow(() -> new RuntimeException(""));
        return result.getTarget();
    }

}
