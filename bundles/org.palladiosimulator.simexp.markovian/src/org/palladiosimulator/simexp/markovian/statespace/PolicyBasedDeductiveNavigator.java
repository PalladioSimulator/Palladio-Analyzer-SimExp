package org.palladiosimulator.simexp.markovian.statespace;

import static org.palladiosimulator.simexp.markovian.util.FilterCriterionUtil.withSource;

import java.util.Set;

import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition;

public class PolicyBasedDeductiveNavigator extends DeductiveStateSpaceNavigator {

	private final Policy<Transition> policy;
	
	public PolicyBasedDeductiveNavigator(MarkovModel markovModel, Policy<Transition> policy) {
		super(markovModel);
		this.policy = policy;
	}

	@Override
	public State navigate(NavigationContext context) {
		Set<Transition> options = markovModelAccessor.filterTransitions(withSource(context.getSource()));
		return policy.select(context.getSource(), options).getTarget();
	}

}
