package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import static java.util.stream.Collectors.toList;
import static org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork.asConditionals;
import static org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork.toConditionalInputs;
import static org.palladiosimulator.simexp.environmentaldynamics.builder.EnvironmentalProcessBuilder.describedBy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.naming.OperationNotSupportedException;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork.InputValue;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork.ConditionalInputValue;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork.Trajectory;
import org.palladiosimulator.pcm.usagemodel.OpenWorkload;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.distribution.factory.ProbabilityDistributionFactory;
import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.environmentaldynamics.builder.EnvironmentModelBuilder;
import org.palladiosimulator.simexp.environmentaldynamics.entity.DerivableEnvironmentalDynamic;
import org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalState;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedValue;
import org.palladiosimulator.simexp.environmentaldynamics.entity.ScalarValue;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.environmentaldynamics.process.ObservableEnvironmentProcess;
import org.palladiosimulator.simexp.markovian.model.builder.BasicMarkovModelBuilder;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.perceiption.PcmAttributeChange;
import org.palladiosimulator.simexp.pcm.perceiption.PcmEnvironmentalState;
import org.palladiosimulator.simexp.pcm.util.ExperimentRunner;

import com.google.common.collect.Lists;

import de.uka.ipd.sdq.probfunction.math.IContinousPDF;
import de.uka.ipd.sdq.probfunction.math.apache.impl.PDFFactory;
import de.uka.ipd.sdq.stoex.StoexPackage;

public class VaryingInterarrivelRateProcess {

	private class IntervallHelper {

		private double lowerBound = 0;
		private double upperBound = 0;
		private double increment = 0;

		public IntervallHelper() {
			this.increment = computeIncrement();
			this.upperBound += this.increment;
		}

		private double computeIncrement() {
			return INTERARRIVAL_RATE_DISTRIBUTION.inverseF(UPPER_PROB_BOUND) / NUMBER_OF_STATES;
		}

		public void updateBounds() {
			lowerBound = upperBound;
			upperBound += increment;
		}

		public double computeProbability() {
			return computeProbability(lowerBound, upperBound);
		}

		public double computeProbability(double lowerBound, double upperBound) {
			return INTERARRIVAL_RATE_DISTRIBUTION.cdf(upperBound) - INTERARRIVAL_RATE_DISTRIBUTION.cdf(lowerBound);
		}

		public double getRepresentative() {
			return getRepresentative(lowerBound, upperBound);
		}

		public double getRepresentative(double lowerBound, double upperBound) {
			return lowerBound + ((upperBound - lowerBound) / 2);
		}

		public boolean upperBoundIsSmallerThan(double value) {
			return upperBound < value;
		}

		public boolean isInbetween(double value) {
			return lowerBound < value && value < upperBound;
		}

		public String getIntervalDescription() {
			return getIntervalDescription(lowerBound, upperBound);
		}

		public String getIntervalDescription(double lowerBound, double upperBound) {
			return String.format("%1s < x <= %2s", Double.toString(lowerBound), Double.toString(upperBound));
		}

		public double getUpperBound() {
			return upperBound;
		}

	}

	private class EnvironmentalStateSpace {

		private final List<Pair<EnvironmentalState, Double>> stateSpace;

		public EnvironmentalStateSpace() {
			this.stateSpace = new ArrayList<>();
		}

		public void createAndAddState(IntervallHelper intervallHelper) {
			if (intervallHelper.upperBoundIsSmallerThan(INTERARRIVAL_TIME_UPPER_BOUND)) {
				intervallHelper.updateBounds();
				return;
			} else if (intervallHelper.isInbetween(INTERARRIVAL_TIME_UPPER_BOUND)) {
				createAndAddState(intervallHelper.getIntervalDescription(0, INTERARRIVAL_TIME_UPPER_BOUND),
						INTERARRIVAL_TIME_UPPER_BOUND,
						intervallHelper.computeProbability(0, INTERARRIVAL_TIME_UPPER_BOUND));
				createAndAddState(
						intervallHelper.getIntervalDescription(INTERARRIVAL_TIME_UPPER_BOUND,
								intervallHelper.getUpperBound()),
						intervallHelper.getRepresentative(INTERARRIVAL_TIME_UPPER_BOUND,
								intervallHelper.getUpperBound()),
						intervallHelper.computeProbability(INTERARRIVAL_TIME_UPPER_BOUND,
								intervallHelper.getUpperBound()));
			} else {
				createAndAddState(intervallHelper.getIntervalDescription(), intervallHelper.getRepresentative(),
						intervallHelper.computeProbability());
			}

			intervallHelper.updateBounds();
		}

		private void createAndAddState(String name, double representative, double probability) {
			stateSpace.add(Pair.of(create(name, representative), probability));
		}

		private EnvironmentalState create(String name, double value) {
			ScalarValue scala = new ScalarValue(ATTRIBUTE_NAME, value);
			PcmEnvironmentalState state = new PcmEnvironmentalState(attrChange, scala);
			state.setName(name);
			return state;
		}

		public List<EnvironmentalState> asList() {
			return stateSpace.stream().map(each -> each.getFirst()).collect(Collectors.toList());
		}

		public Set<ProbabilityMassFunction.Sample> asSamples() {
			return stateSpace.stream().map(toSample()).collect(Collectors.toSet());
		}

		private Function<Pair<EnvironmentalState, Double>, ProbabilityMassFunction.Sample> toSample() {
			return pair -> ProbabilityMassFunction.Sample.of(pair.getFirst(), pair.getSecond());
		}

	}

	private final static double RATE = 1.5;
	private final static double UPPER_PROB_BOUND = 0.999;
	private final static double INTERARRIVAL_TIME_UPPER_BOUND = 0.15;
	private final static int NUMBER_OF_STATES = 100;
	private final static IContinousPDF INTERARRIVAL_RATE_DISTRIBUTION = new PDFFactory()
			.createExponentialDistribution(RATE);
	private final static String ATTRIBUTE_NAME = StoexPackage.Literals.RANDOM_VARIABLE__SPECIFICATION.getName();

	private static PcmAttributeChange attrChange;
	private static VaryingInterarrivelRateProcess processInstance = null;

	private final EnvironmentProcess envProcess;
	private final ProbabilityMassFunction initialDist;

	public VaryingInterarrivelRateProcess(DynamicBayesianNetwork dbn) {
		attrChange = new PcmAttributeChange(retrieveInterArrivalTimeRandomVariableHandler(), ATTRIBUTE_NAME);

		this.initialDist = createInitialDist(dbn);
		this.envProcess = createEnvironmentalProcess(dbn);
	}

	public VaryingInterarrivelRateProcess() {
		attrChange = new PcmAttributeChange(retrieveInterArrivalTimeRandomVariableHandler(), ATTRIBUTE_NAME);

		EnvironmentalStateSpace stateSpace = createStateSpace();
		this.initialDist = createInitialDistributionOver(stateSpace.asSamples());
		this.envProcess = createEnvironmentalProcess(stateSpace.asList());
	}

	private Function<ExperimentRunner, EObject> retrieveInterArrivalTimeRandomVariableHandler() {
		return expRunner -> {
			// TODO exception handling
			PCMResourceSetPartition pcm = expRunner.getReconfigurationPartition();
			OpenWorkload workload = (OpenWorkload) pcm.getUsageModel().getUsageScenario_UsageModel().get(0)
					.getWorkload_UsageScenario();
			return workload.getInterArrivalTime_OpenWorkload();
		};
	}

	public static EnvironmentProcess get(DynamicBayesianNetwork dbn) {
		if (processInstance == null) {
			processInstance = new VaryingInterarrivelRateProcess(dbn);
		}
		return processInstance.envProcess;
	}

	private EnvironmentProcess createEnvironmentalProcess(DynamicBayesianNetwork dbn) {
		return new ObservableEnvironmentProcess(createDerivableProcess(dbn), initialDist);
	}

	private ProbabilityMassFunction createInitialDist(DynamicBayesianNetwork dbn) {
		return new ProbabilityMassFunction() {

			private final BayesianNetwork bn = dbn.getBayesianNetwork();

			@Override
			public Sample drawSample() {
				List<InputValue> sample = bn.sample();
				return Sample.of(asPcmEnvironmentalState(sample), bn.probability(sample));
			}

			@Override
			public double probability(Sample sample) {
				List<InputValue> inputs = toInputs(sample);
				if (inputs.isEmpty()) {
					return 0;
				}
				return bn.probability(inputs);
			}
		};
	}

	private DerivableEnvironmentalDynamic createDerivableProcess(DynamicBayesianNetwork dbn) {
		return new DerivableEnvironmentalDynamic() {

			private boolean explorationMode = false;

			@Override
			public void pursueExplorationStrategy() {
				explorationMode = true;
			}

			@Override
			public void pursueExploitationStrategy() {
				explorationMode = false;
			}

			@Override
			public EnvironmentalState navigate(NavigationContext context) {
				EnvironmentalState envState = EnvironmentalState.class.cast(context.getSource());
				List<InputValue> inputs = toInputs(envState.getValue().getValue());
				if (explorationMode) {
					return sampleRandomly(toConditionalInputs(inputs));
				}
				return sample(toConditionalInputs(inputs));
			}

			private EnvironmentalState sample(List<ConditionalInputValue> conditionalInputs) {
				Trajectory traj = dbn.given(asConditionals(conditionalInputs)).sample();
				return asPcmEnvironmentalState(traj.valueAtTime(0));
			}

			private EnvironmentalState sampleRandomly(List<ConditionalInputValue> conditionalInputs) {
				throw new RuntimeException(new OperationNotSupportedException("The method is not implemented yet."));
			}

		};
	}

	private List<InputValue> toInputs(Object sample) {
		if (List.class.isInstance(sample)) {
			List<?> inputs = List.class.cast(sample);
			if (inputs.isEmpty() == false) {
				if (InputValue.class.isInstance(inputs.get(0))) {
					return inputs.stream().map(InputValue.class::cast).collect(toList());
				}
			}
		}
		return Lists.newArrayList();
	}

	private EnvironmentalState asPcmEnvironmentalState(List<InputValue> sample) {
		//return EnvironmentalState.get(asPerceivedValue(sample));
		return new PcmEnvironmentalState(attrChange, asPerceivedValue(sample));
	}

	private PerceivedValue<?> asPerceivedValue(List<InputValue> sample) {
		return new PerceivedValue<List<InputValue>>() {

			@Override
			public List<InputValue> getValue() {
				return sample;
			}

			@Override
			public Optional<Object> getElement(String key) {
				return Optional.of(sample.get(0).value.get());
			}

			@Override
			public String toString() {
				StringBuilder builder = new StringBuilder();
				for (InputValue each : sample) {
					builder.append(String.format("(Variable: %1s, Value: %2s),", each.variable.getEntityName(),
							each.value.toString()));
				}

				String stringValues = builder.toString();
				return String.format("Samples: [%s])", stringValues.substring(0, stringValues.length() - 1));
			}

		};
	}

	public static EnvironmentProcess get() {
		if (processInstance == null) {
			processInstance = new VaryingInterarrivelRateProcess();
		}
		return processInstance.envProcess;
	}

	private EnvironmentProcess createEnvironmentalProcess(List<EnvironmentalState> stateSpace) {
		return describedBy(createEnvironmentModel(stateSpace)).andInitiallyDistributedWith(initialDist).build();

	}

	private MarkovModel createEnvironmentModel(List<EnvironmentalState> stateSpace) {
		BasicMarkovModelBuilder builder = EnvironmentModelBuilder.get().withStateSpace(new HashSet<State>(stateSpace));
		buildTransitions(builder, stateSpace);
		return builder.build();
	}

	private EnvironmentalStateSpace createStateSpace() {
		IntervallHelper intervallHelper = new IntervallHelper();
		EnvironmentalStateSpace stateSpace = new EnvironmentalStateSpace();
		for (int i = 0; i < NUMBER_OF_STATES; i++) {
			stateSpace.createAndAddState(intervallHelper);
		}
		return stateSpace;
	}

	private void buildTransitions(BasicMarkovModelBuilder builder, List<EnvironmentalState> stateSpace) {
		buildTransitionsForFirstStates(builder, stateSpace);
		for (int i = 1; i <= stateSpace.size() - 2; i++) {
			buildTransitionsForStatesInbetween(builder, stateSpace, i);
		}
		buildTransitionsForLastStates(builder, stateSpace);
	}

	private void buildTransitionsForFirstStates(BasicMarkovModelBuilder builder, List<EnvironmentalState> stateSpace) {
		EnvironmentalState source = stateSpace.get(0);
		EnvironmentalState target = stateSpace.get(1);
		buildTransitionBetween(source, target, builder);
	}

	private void buildTransitionsForLastStates(BasicMarkovModelBuilder builder, List<EnvironmentalState> stateSpace) {
		EnvironmentalState source = stateSpace.get(stateSpace.size() - 1);
		EnvironmentalState target = stateSpace.get(stateSpace.size() - 2);
		buildTransitionBetween(source, target, builder);
	}

	private void buildTransitionBetween(EnvironmentalState source, EnvironmentalState target,
			BasicMarkovModelBuilder builder) {
		double probability = initialDist.probability(ProbabilityMassFunction.Sample.of(source));
		builder = builder.andTransitionBetween(source, source).andProbability(probability);
		builder = builder.andTransitionBetween(source, target).andProbability((1 - probability));
	}

	private void buildTransitionsForStatesInbetween(BasicMarkovModelBuilder builder,
			List<EnvironmentalState> stateSpace, int index) {
		EnvironmentalState last = stateSpace.get(index - 1);
		EnvironmentalState source = stateSpace.get(index);
		EnvironmentalState target = stateSpace.get(index + 1);
		double probability = initialDist.probability(ProbabilityMassFunction.Sample.of(source));
		builder = builder.andTransitionBetween(source, source).andProbability(probability);
		builder = builder.andTransitionBetween(source, target).andProbability((1 - probability) / 2);
		builder = builder.andTransitionBetween(source, last).andProbability((1 - probability) / 2);
	}

	private ProbabilityMassFunction createInitialDistributionOver(Set<ProbabilityMassFunction.Sample> samples) {
		return ProbabilityDistributionFactory.INSTANCE.pmfOver(samples);
	}

}
