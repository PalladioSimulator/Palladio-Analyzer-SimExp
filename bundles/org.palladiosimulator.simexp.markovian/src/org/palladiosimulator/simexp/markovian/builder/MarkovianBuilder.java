package org.palladiosimulator.simexp.markovian.builder;

import java.util.Set;

import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.markovian.activity.ObservationProducer;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.activity.RewardReceiver;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator;
import org.palladiosimulator.simexp.markovian.type.Markovian;

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
	
	public class MDPBuilder <T> implements BasicMarkovianBuilderTemplate<T>, DecisionBasedMarkovionBuilderTemplate<T>, Builder<Markovian<T>> {

		@Override
		public Markovian<T> build() {
			decisionBuilder.decorates(basicBuilder.build());
			return decisionBuilder.build();
		}

		@Override
		public MDPBuilder<T> calculateRewardWith(RewardReceiver rewardCalc) {
			decisionBuilder.calculateRewardWith(rewardCalc);
			return this;
		}

		@Override
		public MDPBuilder<T> withActionSpace(Set<Action<?>> actions) {
			decisionBuilder.withActionSpace(actions);
			return this;
		}

		@Override
		public MDPBuilder<T> selectActionsAccordingTo(Policy<Action<?>> policy) {
			decisionBuilder.selectActionsAccordingTo(policy);
			return this;
		}

		@Override
		public MDPBuilder<T> createStateSpaceNavigator(StateSpaceNavigator stateSpaceNavigator) {
			basicBuilder.createStateSpaceNavigator(stateSpaceNavigator);
			return this;
		}

		@Override
		public MDPBuilder<T> withInitialStateDistribution(ProbabilityMassFunction<T> initialDistribution) {
			basicBuilder.withInitialStateDistribution(initialDistribution);
			return this;
		}
	}

	public class POMDPBuilder <T> implements BasicMarkovianBuilderTemplate<POMDPBuilder<T>>, DecisionBasedMarkovionBuilderTemplate<POMDPBuilder<T>>, HiddenStateMarkovianBuilderTemplate<POMDPBuilder<T>>, Builder<Markovian<T>> {

		@Override
		public Markovian<T> build() {
			decisionBuilder.decorates(basicBuilder.build());
			hiddenBuilder.decorates(decisionBuilder.build());
			return hiddenBuilder.build();
		}

		@Override
		public POMDPBuilder<T> handleObservationsWith(ObservationProducer obsHandler) {
			hiddenBuilder.handleObservationsWith(obsHandler);
			return this;
		}

		@Override
		public POMDPBuilder<T> calculateRewardWith(RewardReceiver rewardCalc) {
			decisionBuilder.calculateRewardWith(rewardCalc);
			return this;
		}

		@Override
		public POMDPBuilder<T> withActionSpace(Set<Action<?>> actions) {
			decisionBuilder.withActionSpace(actions);
			return this;
		}

		@Override
		public POMDPBuilder<T> selectActionsAccordingTo(Policy<Action<?>> policy) {
			decisionBuilder.selectActionsAccordingTo(policy);
			return this;
		}

		@Override
		public POMDPBuilder<T> createStateSpaceNavigator(StateSpaceNavigator stateSpaceNavigator) {
			basicBuilder.createStateSpaceNavigator(stateSpaceNavigator);
			return this;
		}

		@Override
		public POMDPBuilder<T> withInitialStateDistribution(ProbabilityMassFunction<POMDPBuilder<T>> initialDistribution) {
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
