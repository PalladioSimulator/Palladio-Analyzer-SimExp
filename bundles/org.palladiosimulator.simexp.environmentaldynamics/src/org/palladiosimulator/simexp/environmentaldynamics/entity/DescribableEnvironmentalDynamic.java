package org.palladiosimulator.simexp.environmentaldynamics.entity;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.exploitation.ProbabilityBasedTransitionPolicy;
import org.palladiosimulator.simexp.markovian.exploration.RandomizedStrategy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Transition;
import org.palladiosimulator.simexp.markovian.statespace.PolicyBasedDeductiveNavigator;

public class DescribableEnvironmentalDynamic extends PolicyBasedDeductiveNavigator implements EnvironmentalDynamic {

	private static class PolicyController implements Policy<Transition> {

		private final static String POLICY_NAME = "PolicyControllerOf";

		private final static int EXPLOITATION_INDEX = 0;
		private final static int EXPLORATION_INDEX = 1;
		private final static PolicyController controllerInstance = new PolicyController();

		public static PolicyController get() {
			return controllerInstance;
		}

		private final List<Policy<Transition>> strategies;
		private int chosenStrategyIndex;

		private PolicyController() {
			this.strategies = Arrays.asList(new RandomizedStrategy<Transition>(),
					new ProbabilityBasedTransitionPolicy());
			this.chosenStrategyIndex = EXPLOITATION_INDEX;
		}

		@Override
		public Transition select(State source, Set<Transition> options) {
			return strategies.get(chosenStrategyIndex).select(source, options);
		}

		public void pursueExplorationStrategy() {
			chosenStrategyIndex = EXPLORATION_INDEX;
		}

		public void pursueExploitationStrategy() {
			chosenStrategyIndex = EXPLOITATION_INDEX;
		}

		@Override
		public String getId() {
			return String.format("%1s%2sAnd%3s", POLICY_NAME, strategies.get(0).getId(), strategies.get(1).getId());
		}

	}
	
	private final boolean isHiddenProcess;
	
	protected DescribableEnvironmentalDynamic(MarkovModel markovModel, boolean isHiddenProcess) {
		super(markovModel, PolicyController.get());
		
		this.isHiddenProcess = isHiddenProcess;
	}

	@Override
	public boolean isHiddenProcess() {
		return isHiddenProcess;
	}

	@Override
	public void pursueExplorationStrategy() {
		PolicyController.get().pursueExplorationStrategy();
	}

	@Override
	public void pursueExploitationStrategy() {
		PolicyController.get().pursueExploitationStrategy();
	}
}
