package de.fzi.srp.core.process.statespace;

import static de.fzi.srp.core.process.util.FilterCriterionUtil.withSource;

import java.util.Set;

import de.fzi.srp.core.model.markovmodel.markoventity.MarkovModel;
import de.fzi.srp.core.model.markovmodel.markoventity.State;
import de.fzi.srp.core.model.markovmodel.markoventity.Transition;
import de.fzi.srp.core.process.markovian.activity.Policy;

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
