package de.fzi.srp.core.process.builder;

import java.util.Set;

import de.fzi.srp.core.model.markovmodel.markoventity.Action;
import de.fzi.srp.core.process.markovian.Markovian;
import de.fzi.srp.core.process.markovian.activity.ObservationProducer;
import de.fzi.srp.core.process.markovian.activity.Policy;
import de.fzi.srp.core.process.markovian.activity.RewardReceiver;
import de.fzi.srp.core.process.statespace.StateSpaceNavigator;
import de.fzi.srp.distribution.function.ProbabilityMassFunction;

public class MarkovianBuilder {

	public class MarkovChainBuilder implements BasicMarkovianBuilderTemplate<MarkovChainBuilder>, Builder<Markovian> {

		@Override
		public Markovian build() {
			return basicBuilder.build();
		}

		@Override
		public MarkovChainBuilder createStateSpaceNavigator(StateSpaceNavigator stateSpaceNavigator) {
			basicBuilder.createStateSpaceNavigator(stateSpaceNavigator);
			return this;
		}

		@Override
		public MarkovChainBuilder withInitialStateDistribution(ProbabilityMassFunction initialDistribution) {
			basicBuilder.withInitialStateDistribution(initialDistribution);
			return this;
		}
	}
	
	public class HMMBuilder implements BasicMarkovianBuilderTemplate<HMMBuilder>, HiddenStateMarkovianBuilderTemplate<HMMBuilder>, Builder<Markovian> {

		@Override
		public Markovian build() {
			hiddenBuilder.decorates(basicBuilder.build());
			return hiddenBuilder.build();
		}

		@Override
		public HMMBuilder handleObservationsWith(ObservationProducer obsHandler) {
			hiddenBuilder.handleObservationsWith(obsHandler);
			return this;
		}

		@Override
		public HMMBuilder createStateSpaceNavigator(StateSpaceNavigator stateSpaceNavigator) {
			basicBuilder.createStateSpaceNavigator(stateSpaceNavigator);
			return this;
		}

		@Override
		public HMMBuilder withInitialStateDistribution(ProbabilityMassFunction initialDistribution) {
			basicBuilder.withInitialStateDistribution(initialDistribution);
			return this;
		}
	}
	
	public class MDPBuilder implements BasicMarkovianBuilderTemplate<MDPBuilder>, DecisionBasedMarkovionBuilderTemplate<MDPBuilder>, Builder<Markovian> {

		@Override
		public Markovian build() {
			decisionBuilder.decorates(basicBuilder.build());
			return decisionBuilder.build();
		}

		@Override
		public MDPBuilder calculateRewardWith(RewardReceiver rewardCalc) {
			decisionBuilder.calculateRewardWith(rewardCalc);
			return this;
		}

		@Override
		public MDPBuilder withActionSpace(Set<Action<?>> actions) {
			decisionBuilder.withActionSpace(actions);
			return this;
		}

		@Override
		public MDPBuilder selectActionsAccordingTo(Policy<Action<?>> policy) {
			decisionBuilder.selectActionsAccordingTo(policy);
			return this;
		}

		@Override
		public MDPBuilder createStateSpaceNavigator(StateSpaceNavigator stateSpaceNavigator) {
			basicBuilder.createStateSpaceNavigator(stateSpaceNavigator);
			return this;
		}

		@Override
		public MDPBuilder withInitialStateDistribution(ProbabilityMassFunction initialDistribution) {
			basicBuilder.withInitialStateDistribution(initialDistribution);
			return this;
		}
	}

	public class POMDPBuilder implements BasicMarkovianBuilderTemplate<POMDPBuilder>, DecisionBasedMarkovionBuilderTemplate<POMDPBuilder>, HiddenStateMarkovianBuilderTemplate<POMDPBuilder>, Builder<Markovian> {

		@Override
		public Markovian build() {
			decisionBuilder.decorates(basicBuilder.build());
			hiddenBuilder.decorates(decisionBuilder.build());
			return hiddenBuilder.build();
		}

		@Override
		public POMDPBuilder handleObservationsWith(ObservationProducer obsHandler) {
			hiddenBuilder.handleObservationsWith(obsHandler);
			return this;
		}

		@Override
		public POMDPBuilder calculateRewardWith(RewardReceiver rewardCalc) {
			decisionBuilder.calculateRewardWith(rewardCalc);
			return this;
		}

		@Override
		public POMDPBuilder withActionSpace(Set<Action<?>> actions) {
			decisionBuilder.withActionSpace(actions);
			return this;
		}

		@Override
		public POMDPBuilder selectActionsAccordingTo(Policy<Action<?>> policy) {
			decisionBuilder.selectActionsAccordingTo(policy);
			return this;
		}

		@Override
		public POMDPBuilder createStateSpaceNavigator(StateSpaceNavigator stateSpaceNavigator) {
			basicBuilder.createStateSpaceNavigator(stateSpaceNavigator);
			return this;
		}

		@Override
		public POMDPBuilder withInitialStateDistribution(ProbabilityMassFunction initialDistribution) {
			basicBuilder.withInitialStateDistribution(initialDistribution);
			return this;
		}
	}
	
	private BasicMarkovianBuilder basicBuilder;
	private HiddenStateMarkovianBuilder hiddenBuilder;
	private DecisionBasedMarkovianBuilder decisionBuilder;
	
	private MarkovianBuilder() {
		this.basicBuilder = BasicMarkovianBuilder.createBasicMarkovian();
		this.hiddenBuilder = HiddenStateMarkovianBuilder.createHiddenStateMarkovianBuilder();
		this.decisionBuilder = DecisionBasedMarkovianBuilder.createDecisionBasedMarkovianBuilder();
	}
	
	private static MarkovianBuilder createBuilder() {
		return new MarkovianBuilder();
	}
	
	public static MarkovChainBuilder createMarkovChain() {
		return createBuilder().new MarkovChainBuilder();
	}
	
	public static HMMBuilder createHiddenMarkovModel() {
		return createBuilder().new HMMBuilder();
	}
	
	public static MDPBuilder createMarkovDecisionProcess() {
		return createBuilder().new MDPBuilder();
	}
	
	public static POMDPBuilder createPartiallyObservableMDP() {
		return createBuilder().new POMDPBuilder();
	}
}
