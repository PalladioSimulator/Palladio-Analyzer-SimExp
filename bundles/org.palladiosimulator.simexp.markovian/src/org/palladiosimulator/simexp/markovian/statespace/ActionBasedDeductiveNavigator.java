package org.palladiosimulator.simexp.markovian.statespace;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition;

public class ActionBasedDeductiveNavigator<A, R> extends DeductiveStateSpaceNavigator<A, R> {

    public ActionBasedDeductiveNavigator(MarkovModel<A, R> markovModel) {
        super(markovModel);
    }

    @Override
    public State navigate(NavigationContext<A> context) {
        // TODO exception handling
        if (context.getAction()
            .isPresent() == false) {
            throw new RuntimeException("");
        }

        Transition<A> result = markovModelAccessor.findTransition(context.getSource(), context.getAction()
            .get())
            .orElseThrow(() -> new RuntimeException(""));
        return result.getTarget();
    }

}
