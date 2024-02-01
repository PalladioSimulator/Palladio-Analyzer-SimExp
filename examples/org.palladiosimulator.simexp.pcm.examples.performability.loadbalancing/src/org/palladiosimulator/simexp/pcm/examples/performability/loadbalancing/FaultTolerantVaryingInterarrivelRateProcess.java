package org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing;

import static java.util.stream.Collectors.toList;
import static org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork.asConditionals;
import static org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork.toConditionalInputs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

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
import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.environmentaldynamics.entity.DerivableEnvironmentalDynamic;
import org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalState;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedValue;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.environmentaldynamics.process.ObservableEnvironmentProcess;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.binding.api.PcmModelChangeFactory;
import org.palladiosimulator.simexp.pcm.perceiption.PcmAttributeChange;
import org.palladiosimulator.simexp.pcm.perceiption.PcmEnvironmentalState;
import org.palladiosimulator.simexp.pcm.perceiption.PcmModelChange;
import org.palladiosimulator.simexp.pcm.util.ExperimentRunner;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.uka.ipd.sdq.stoex.StoexPackage;

public class FaultTolerantVaryingInterarrivelRateProcess<S, A, Aa extends Action<A>, R> {

    private static final Logger LOGGER = Logger.getLogger(FaultTolerantVaryingInterarrivelRateProcess.class.getName());

    private final static String PCM_SPECIFICATION_ATTRIBUTE = StoexPackage.Literals.RANDOM_VARIABLE__SPECIFICATION
        .getName();
    private final static String WORKLOAD_VARIABLE = "GRV_StaticInstance_VaryingWorkload"; // GRV
                                                                                          // entity
                                                                                          // name
                                                                                          // *.staticmodel

    private final static String PCM_RESOURCE_CONTAINER_SERVER_1_ATTRIBUTE = "ServerNode1"; // entityName
                                                                                           // pcm
                                                                                           // resource
                                                                                           // container
                                                                                           // representing
                                                                                           // server
                                                                                           // node 1
    private final static String SERVER_NODE_1_VARIABLE = "GRV_StaticInstance_ServerNode1"; // GRV
                                                                                           // entity
                                                                                           // name
                                                                                           // *.staticmodel
    private final static String PCM_RESOURCE_CONTAINER_SERVER_2_ATTRIBUTE = "ServerNode2"; // entityName
                                                                                           // pcm
                                                                                           // resource
                                                                                           // container
                                                                                           // representing
                                                                                           // server
                                                                                           // node 2
    private final static String SERVER_NODE_2_VARIABLE = "GRV_StaticInstance_ServerNode2"; // GVR
                                                                                           // entity
                                                                                           // name
                                                                                           // *.staticmodel

    private final PcmAttributeChange<List<InputValue>> attrChange;
    private final PcmModelChange<List<InputValue>> attrChangeServerNode1;
    private final PcmModelChange<List<InputValue>> attrChangeServerNode2;
    private final EnvironmentProcess<S, A, R, List<InputValue>> envProcess;
    private final ProbabilityMassFunction<State<S>> initialDist;

    public FaultTolerantVaryingInterarrivelRateProcess(DynamicBayesianNetwork dbn,
            IExperimentProvider experimentProvider) {
        this.attrChange = new PcmAttributeChange<>(retrieveInterArrivalTimeRandomVariableHandler(),
                PCM_SPECIFICATION_ATTRIBUTE, experimentProvider);
        // attribute name values are taken from the names of the instantiated template variable
        // model, i.e. *.staticmodel
        attrChangeServerNode1 = PcmModelChangeFactory
            .createResourceContainerPcmModelChange(PCM_RESOURCE_CONTAINER_SERVER_1_ATTRIBUTE, experimentProvider);
        attrChangeServerNode2 = PcmModelChangeFactory
            .createResourceContainerPcmModelChange(PCM_RESOURCE_CONTAINER_SERVER_2_ATTRIBUTE, experimentProvider);
        this.initialDist = createInitialDist(dbn);
        this.envProcess = createEnvironmentalProcess(dbn);
    }

    public EnvironmentProcess<S, A, R, List<InputValue>> getEnvironmentProcess() {
        return envProcess;
    }

    private Function<ExperimentRunner, EObject> retrieveInterArrivalTimeRandomVariableHandler() {
        return expRunner -> {
            // TODO exception handling
            PCMResourceSetPartition pcm = expRunner.getWorkingPartition();
            OpenWorkload workload = (OpenWorkload) pcm.getUsageModel()
                .getUsageScenario_UsageModel()
                .get(0)
                .getWorkload_UsageScenario();
            return workload.getInterArrivalTime_OpenWorkload();
        };
    }

    private EnvironmentProcess<S, A, R, List<InputValue>> createEnvironmentalProcess(DynamicBayesianNetwork dbn) {
        return new ObservableEnvironmentProcess<>(createDerivableProcess(dbn), initialDist);
    }

    private ProbabilityMassFunction<State<S>> createInitialDist(DynamicBayesianNetwork dbn) {
        return new ProbabilityMassFunction<>() {

            private final BayesianNetwork bn = dbn.getBayesianNetwork();

            @Override
            public Sample<State<S>> drawSample() {
                List<InputValue> sample = bn.sample();
                return Sample.of(asPcmEnvironmentalState(sample), bn.probability(sample));
            }

            @Override
            public double probability(Sample<State<S>> sample) {
                List<InputValue> inputs = toInputs(sample);
                if (inputs.isEmpty()) {
                    return 0;
                }
                return bn.probability(inputs);
            }
        };
    }

    private DerivableEnvironmentalDynamic<S, A> createDerivableProcess(DynamicBayesianNetwork dbn) {
        return new DerivableEnvironmentalDynamic<>() {

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
            public EnvironmentalState<S, List<InputValue>> navigate(NavigationContext<S, A> context) {
                EnvironmentalState<S, List<InputValue>> envState = EnvironmentalState.class.cast(context.getSource());
                List<InputValue> inputs = toInputs(envState.getValue()
                    .getValue());
                if (explorationMode) {
                    return sampleRandomly(toConditionalInputs(inputs));
                }
                return sample(toConditionalInputs(inputs));
            }

            private EnvironmentalState<S, List<InputValue>> sample(List<ConditionalInputValue> conditionalInputs) {
                Trajectory traj = dbn.given(asConditionals(conditionalInputs))
                    .sample();
                return asPcmEnvironmentalState(traj.valueAtTime(0));
            }

            private EnvironmentalState<S, List<InputValue>> sampleRandomly(
                    List<ConditionalInputValue> conditionalInputs) {
                throw new RuntimeException(new OperationNotSupportedException("The method is not implemented yet."));
            }

        };
    }

    public static List<InputValue> toInputs(Object sample) {
        if (List.class.isInstance(sample)) {
            List<?> inputs = List.class.cast(sample);
            if (inputs.isEmpty() == false) {
                if (InputValue.class.isInstance(inputs.get(0))) {
                    return inputs.stream()
                        .map(InputValue.class::cast)
                        .collect(toList());
                }
            }
        }
        return Lists.newArrayList();
    }

    private EnvironmentalState<S, List<InputValue>> asPcmEnvironmentalState(List<InputValue> sample) {
        // return EnvironmentalState.get(asPerceivedValue(sample));
        ArrayList<PcmModelChange<List<InputValue>>> attrChanges = new ArrayList<>();
        attrChanges.add(attrChange);
        attrChanges.add(attrChangeServerNode1);
        attrChanges.add(attrChangeServerNode2);
        return new PcmEnvironmentalState<>(attrChanges, asPerceivedValue(sample));
    }

    private PerceivedValue<List<InputValue>> asPerceivedValue(List<InputValue> sample) {
        Map<String, InputValue> newValueStore = Maps.newHashMap();
        newValueStore.put(PCM_SPECIFICATION_ATTRIBUTE, findInputValue(sample, WORKLOAD_VARIABLE));
        newValueStore.put(PCM_RESOURCE_CONTAINER_SERVER_1_ATTRIBUTE, findInputValue(sample, SERVER_NODE_1_VARIABLE));
        newValueStore.put(PCM_RESOURCE_CONTAINER_SERVER_2_ATTRIBUTE, findInputValue(sample, SERVER_NODE_2_VARIABLE));

        return new PerceivedValue<>() {

            private final Map<String, InputValue> valueStore = newValueStore;

            @Override
            public List<InputValue> getValue() {
                return valueStore.values()
                    .stream()
                    .map(InputValue.class::cast)
                    .collect(toList());
            }

            @Override
            public Optional<Object> getElement(String key) {
                return Optional.ofNullable(valueStore.get(key))
                    .map(InputValue::asCategorical);
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
            .orElseThrow(() -> new RuntimeException(
                    String.format("Variable not found in sample | variableName: '%s' | sample: %s ", variableName,
                            StringUtils.join(sample, ","))));
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

}
