package de.fzi.srp.core.process.builder;

import java.util.Objects;
import java.util.Optional;

import de.fzi.srp.core.model.markovmodel.markoventity.MarkovModel;
import de.fzi.srp.core.model.markovmodel.markoventity.Transition;
import de.fzi.srp.core.process.markovian.activity.Policy;
import de.fzi.srp.core.process.statespace.ActionBasedDeductiveNavigator;
import de.fzi.srp.core.process.statespace.InductiveStateSpaceNavigator;
import de.fzi.srp.core.process.statespace.PolicyBasedDeductiveNavigator;
import de.fzi.srp.core.process.statespace.StateSpaceNavigator;

public class StateSpaceNavigatorBuilder {
	
	public static class InductiveStateSpaceNavigatorBuilder {
		
		private InductiveStateSpaceNavigator inductiveNav;
		
		public InductiveStateSpaceNavigatorBuilder inductiveNavigationThrough(InductiveStateSpaceNavigator inductiveNav) {
			this.inductiveNav = inductiveNav;
			return this;
		}
		
		public StateSpaceNavigator build() {
			//TODO exception handling
			Objects.requireNonNull(inductiveNav, "");
			
			return inductiveNav;
		}
		
	}
	
	public static class DeductiveStateSpaceNavigatorBuilder {
		
		private MarkovModel markovModel;
		private Optional<Policy<Transition>> policy = Optional.empty();
		
		public DeductiveStateSpaceNavigatorBuilder(MarkovModel markovModel) {
			this.markovModel = markovModel;
		}
		
		public DeductiveStateSpaceNavigatorBuilder withTransitionPolicy(Policy<Transition> policy) {
			this.policy = Optional.ofNullable(policy);
			return this;
		}
		
		public StateSpaceNavigator build() {
			//TODO exception handling
			Objects.requireNonNull(markovModel, "");
			
			if (policy.isPresent()) {
				return new PolicyBasedDeductiveNavigator(markovModel, policy.get());
			}
			return new ActionBasedDeductiveNavigator(markovModel);
		}
		
	}

	public static InductiveStateSpaceNavigatorBuilder createStateSpaceNavigator() {
		return new InductiveStateSpaceNavigatorBuilder();
	}
	
	public static DeductiveStateSpaceNavigatorBuilder createStateSpaceNavigator(MarkovModel markovModel) {
		return new DeductiveStateSpaceNavigatorBuilder(markovModel);
	}
	
}
