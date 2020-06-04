package org.palladiosimulator.simexp.markovian.builder;

import java.util.Objects;
import java.util.Optional;

import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition;
import org.palladiosimulator.simexp.markovian.statespace.ActionBasedDeductiveNavigator;
import org.palladiosimulator.simexp.markovian.statespace.InductiveStateSpaceNavigator;
import org.palladiosimulator.simexp.markovian.statespace.PolicyBasedDeductiveNavigator;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator;

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
