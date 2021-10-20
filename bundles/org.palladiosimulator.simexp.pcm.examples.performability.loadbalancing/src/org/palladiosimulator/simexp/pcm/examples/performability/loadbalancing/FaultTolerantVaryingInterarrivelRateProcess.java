package org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing;

import static java.util.stream.Collectors.toList;
import static org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork.asConditionals;
import static org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork.toConditionalInputs;
import static org.palladiosimulator.simexp.environmentaldynamics.builder.EnvironmentalProcessBuilder.describedBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.naming.OperationNotSupportedException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork.InputValue;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork.ConditionalInputValue;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork.Trajectory;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundRandomVariable;
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
import org.palladiosimulator.simexp.pcm.binding.api.PcmModelChangeFactory;
import org.palladiosimulator.simexp.pcm.perceiption.PcmAttributeChange;
import org.palladiosimulator.simexp.pcm.perceiption.PcmEnvironmentalState;
import org.palladiosimulator.simexp.pcm.perceiption.PcmModelChange;
import org.palladiosimulator.simexp.pcm.util.ExperimentProvider;
import org.palladiosimulator.simexp.pcm.util.ExperimentRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.uka.ipd.sdq.probfunction.math.IContinousPDF;
import de.uka.ipd.sdq.probfunction.math.apache.impl.PDFFactory;
import de.uka.ipd.sdq.stoex.StoexPackage;

public class FaultTolerantVaryingInterarrivelRateProcess {
    
    private static final Logger LOGGER = Logger.getLogger(FaultTolerantVaryingInterarrivelRateProcess.class.getName());

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
			ScalarValue scala = new ScalarValue(PCM_SPECIFICATION_ATTRIBUTE, value);
			PcmEnvironmentalState state = new PcmEnvironmentalState(Arrays.asList(attrChange), scala);
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
	private final static String PCM_SPECIFICATION_ATTRIBUTE = StoexPackage.Literals.RANDOM_VARIABLE__SPECIFICATION.getName();
	private final static String WORKLOAD_VARIABLE = "GRV_StaticInstance_VaryingWorkload";     // GRV entity name *.staticmodel
	
	private final static String PCM_RESOURCE_CONTAINER_SERVER_1_ATTRIBUTE = "ServerNode1";  // entityName pcm resource container representing server node 1
	private final static String SERVER_NODE_1_VARIABLE = "GRV_StaticInstance_ServerNode1";  // GRV entity name *.staticmodel
	private final static String PCM_RESOURCE_CONTAINER_SERVER_2_ATTRIBUTE = "ServerNode2";  // entityName pcm resource container representing server node 2
	private final static String SERVER_NODE_2_VARIABLE = "GRV_StaticInstance_ServerNode2";  // GVR entity name *.staticmodel

	private static PcmAttributeChange attrChange;
	private static PcmModelChange attrChangeServerNode1;
	private static PcmModelChange attrChangeServerNode2;
	private static FaultTolerantVaryingInterarrivelRateProcess processInstance = null;

	private final EnvironmentProcess envProcess;
	private final ProbabilityMassFunction initialDist;

	public FaultTolerantVaryingInterarrivelRateProcess(DynamicBayesianNetwork dbn) {
	    initPcmAttributeChange();
		this.initialDist = createInitialDist(dbn);
		this.envProcess = createEnvironmentalProcess(dbn);
	}

	public FaultTolerantVaryingInterarrivelRateProcess() {
	    initPcmAttributeChange();
		EnvironmentalStateSpace stateSpace = createStateSpace();
		this.initialDist = createInitialDistributionOver(stateSpace.asSamples());
		this.envProcess = createEnvironmentalProcess(stateSpace.asList());
	}
	
    private void initPcmAttributeChange() {
        attrChange = new PcmAttributeChange(retrieveInterArrivalTimeRandomVariableHandler(),
                PCM_SPECIFICATION_ATTRIBUTE);
        // attribute name values are taken from the names of the instantiated template variable
        // model, i.e. *.staticmodel
        attrChangeServerNode1 = PcmModelChangeFactory.createResourceContainerPcmModelChange(PCM_RESOURCE_CONTAINER_SERVER_1_ATTRIBUTE);
        attrChangeServerNode2 = PcmModelChangeFactory.createResourceContainerPcmModelChange(PCM_RESOURCE_CONTAINER_SERVER_2_ATTRIBUTE);
    }

	private Function<ExperimentRunner, EObject> retrieveInterArrivalTimeRandomVariableHandler() {
		return expRunner -> {
			// TODO exception handling
			PCMResourceSetPartition pcm = expRunner.getWorkingPartition();
			OpenWorkload workload = (OpenWorkload) pcm.getUsageModel().getUsageScenario_UsageModel().get(0)
					.getWorkload_UsageScenario();
			return workload.getInterArrivalTime_OpenWorkload();
		};
	}


	public static EnvironmentProcess get(DynamicBayesianNetwork dbn) {
		if (processInstance == null) {
			processInstance = new FaultTolerantVaryingInterarrivelRateProcess(dbn);
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

	public static List<InputValue> toInputs(Object sample) {
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
	    ArrayList<PcmModelChange> attrChanges = new ArrayList<PcmModelChange>();
	    attrChanges.add(attrChange);
	    attrChanges.add(attrChangeServerNode1);
	    attrChanges.add(attrChangeServerNode2);
		return new PcmEnvironmentalState(attrChanges, asPerceivedValue(sample));
	}

	private PerceivedValue<?> asPerceivedValue(List<InputValue> sample) {
		Map<String, InputValue> newValueStore = Maps.newHashMap();
		newValueStore.put(PCM_SPECIFICATION_ATTRIBUTE, findInputValue(sample, WORKLOAD_VARIABLE));
		newValueStore.put(PCM_RESOURCE_CONTAINER_SERVER_1_ATTRIBUTE, findInputValue(sample, SERVER_NODE_1_VARIABLE));
		newValueStore.put(PCM_RESOURCE_CONTAINER_SERVER_2_ATTRIBUTE, findInputValue(sample, SERVER_NODE_2_VARIABLE));
		
		return new PerceivedValue<List<InputValue>>() {

			private final Map<String, InputValue> valueStore = newValueStore;
			
			@Override
			public List<InputValue> getValue() {
				return valueStore.values().stream()
						.map(InputValue.class::cast)
						.collect(toList());
			}

			@Override
			public Optional<Object> getElement(String key) {
				return Optional.ofNullable(valueStore.get(key)).map(InputValue::asCategorical);
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

	
	// rewrite; sample + variable name -> 
	private InputValue findInputValue(List<InputValue> sample, String variableName) {
//	private InputValue findWorkloadInputValue(List<InputValue> sample) {
	    Predicate<InputValue> inputValue = inputValue(variableName);
	    
		return sample.stream()
				.filter(inputValue) 
				.findFirst()
				.orElseThrow(() -> new RuntimeException(String.format("Variable not found in sample | variableName: '%s' | sample: %s "
				        , variableName, StringUtils.join(sample, ","))));
	}

	private Predicate<InputValue> inputValue(String variableName) {
//		return i -> i.variable.getInstantiatedTemplate().getEntityName().equals(WORKLOAD_TEMPLATE);
	    return inputValue -> {
	        GroundRandomVariable groundRandomVariabe = inputValue.variable;
	        String groundRandomVariableName = groundRandomVariabe.getEntityName();
	        return groundRandomVariableName.equals(variableName);
	    };
//		return i -> i.variable.getEntityName().equals(variableName);
	}

	public static EnvironmentProcess get() {
		if (processInstance == null) {
			processInstance = new FaultTolerantVaryingInterarrivelRateProcess();
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
