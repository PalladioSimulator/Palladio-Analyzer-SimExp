package org.palladiosimulator.simexp.markovian.statespace;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition;

public class ActionBasedDeductiveNavigator extends DeductiveStateSpaceNavigator {

	public ActionBasedDeductiveNavigator(MarkovModel markovModel) {
		super(markovModel);
	}

	@Override
	public State navigate(NavigationContext context) {
		//TODO exception handling
		if (context.getAction().isPresent() == false) {
			throw new RuntimeException("");
		}
		
		Transition result = markovModelAccessor.findTransition(context.getSource(), context.getAction().get())
											   .orElseThrow(() -> new RuntimeException(""));
		return result.getTarget();
	}

}
