package org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.naming.OperationNotSupportedException;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.ConditionalInputValueUtil;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork.ConditionalInputValue;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork.Trajectory;
import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.pcm.usagemodel.OpenWorkload;
import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.environmentaldynamics.entity.DerivableEnvironmentalDynamic;
import org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalState;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedElement;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedSelectedInputValues;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedValue;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.environmentaldynamics.process.ObservableEnvironmentProcess;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.binding.api.PcmModelChangeFactory;
import org.palladiosimulator.simexp.pcm.perceiption.PcmAttributeChange;
import org.palladiosimulator.simexp.pcm.perceiption.PcmEnvironmentalState;
import org.palladiosimulator.simexp.pcm.perceiption.PcmModelChange;
import org.palladiosimulator.simexp.pcm.perceiption.PerceivedValueConverter;
import org.palladiosimulator.simexp.pcm.util.ExperimentRunner;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.uka.ipd.sdq.stoex.StoexPackage;
import tools.mdsd.probdist.api.entity.CategoricalValue;
import tools.mdsd.probdist.api.random.ISeedProvider;
import tools.mdsd.probdist.api.random.ISeedable;

public class FaultTolerantVaryingInterarrivelRateProcess<C, A, Aa extends Action<A>, R> implements ISeedable {

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

    private final PcmAttributeChange<List<InputValue<CategoricalValue>>> attrChange;
    private final PcmModelChange<List<InputValue<CategoricalValue>>> attrChangeServerNode1;
    private final PcmModelChange<List<InputValue<CategoricalValue>>> attrChangeServerNode2;
    private final EnvironmentProcess<A, R, List<InputValue<CategoricalValue>>> envProcess;
    private final ProbabilityMassFunction<State> initialDist;
    private final ConditionalInputValueUtil<CategoricalValue> conditionalInputValueUtil = new ConditionalInputValueUtil<>();

    private boolean initialized = false;

    public FaultTolerantVaryingInterarrivelRateProcess(DynamicBayesianNetwork<CategoricalValue> dbn,
            IExperimentProvider experimentProvider) {
        PerceivedValueConverter<List<InputValue<CategoricalValue>>> perceivedValueConverter = new PerceivedValueConverter<>() {

            @Override
            public CategoricalValue convertElement(PerceivedValue<List<InputValue<CategoricalValue>>> change,
                    String key) {
                PerceivedElement<List<InputValue<CategoricalValue>>> pe = (PerceivedElement<List<InputValue<CategoricalValue>>>) change;
                Optional<List<InputValue<CategoricalValue>>> values = pe.getElement(key);
                List<InputValue<CategoricalValue>> valueList = values.get();
                InputValue<CategoricalValue> inputValue = valueList.get(0);
                CategoricalValue changedValue = inputValue.getValue();
                return changedValue;
            }
        };

        this.attrChange = new PcmAttributeChange<>(retrieveInterArrivalTimeRandomVariableHandler(),
                PCM_SPECIFICATION_ATTRIBUTE, experimentProvider, perceivedValueConverter);
        // attribute name values are taken from the names of the instantiated template variable
        // model, i.e. *.staticmodel

        attrChangeServerNode1 = PcmModelChangeFactory.createResourceContainerPcmModelChange(
                PCM_RESOURCE_CONTAINER_SERVER_1_ATTRIBUTE, perceivedValueConverter, experimentProvider);
        attrChangeServerNode2 = PcmModelChangeFactory.createResourceContainerPcmModelChange(
                PCM_RESOURCE_CONTAINER_SERVER_2_ATTRIBUTE, perceivedValueConverter, experimentProvider);
        this.initialDist = createInitialDist(dbn);
        this.envProcess = createEnvironmentalProcess(dbn);
    }

    @Override
    public void init(Optional<ISeedProvider> seedProvider) {
        initialized = true;
        initialDist.init(seedProvider);
    }

    public EnvironmentProcess<A, R, List<InputValue<CategoricalValue>>> getEnvironmentProcess() {
        if (!initialized) {
            throw new RuntimeException("not initialized");
        }
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

    private EnvironmentProcess<A, R, List<InputValue<CategoricalValue>>> createEnvironmentalProcess(
            DynamicBayesianNetwork<CategoricalValue> dbn) {
        return new ObservableEnvironmentProcess<>(createDerivableProcess(dbn), null, initialDist);
    }

    private ProbabilityMassFunction<State> createInitialDist(DynamicBayesianNetwork<CategoricalValue> dbn) {
        return new ProbabilityMassFunction<>() {

            private final BayesianNetwork<CategoricalValue> bn = dbn.getBayesianNetwork();

            private boolean initialized = false;

            @Override
            public void init(Optional<ISeedProvider> seedProvider) {
                initialized = true;
                bn.init(seedProvider);
            }

            @Override
            public Sample<State> drawSample() {
                if (!initialized) {
                    throw new RuntimeException("not initialized");
                }
                List<InputValue<CategoricalValue>> sample = bn.sample();
                return Sample.of(asPcmEnvironmentalState(sample), bn.probability(sample));
            }

            @Override
            public double probability(Sample<State> sample) {
                List<InputValue<CategoricalValue>> inputs = toInputs(sample);
                if (inputs.isEmpty()) {
                    return 0;
                }
                return bn.probability(inputs);
            }
        };
    }

    private DerivableEnvironmentalDynamic<A> createDerivableProcess(DynamicBayesianNetwork<CategoricalValue> dbn) {
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
            public EnvironmentalState<List<InputValue<CategoricalValue>>> navigate(NavigationContext<A> context) {
                EnvironmentalState<List<InputValue<CategoricalValue>>> envState = EnvironmentalState.class
                    .cast(context.getSource());
                List<InputValue<CategoricalValue>> inputs = toInputs(envState.getValue()
                    .getValue());
                if (explorationMode) {
                    return sampleRandomly(conditionalInputValueUtil.toConditionalInputs(inputs));
                }
                return sample(conditionalInputValueUtil.toConditionalInputs(inputs));
            }

            private EnvironmentalState<List<InputValue<CategoricalValue>>> sample(
                    List<ConditionalInputValue<CategoricalValue>> conditionalInputs) {
                Trajectory<CategoricalValue> traj = dbn
                    .given(conditionalInputValueUtil.asConditionals(conditionalInputs))
                    .sample();
                return asPcmEnvironmentalState(traj.valueAtTime(0));
            }

            private EnvironmentalState<List<InputValue<CategoricalValue>>> sampleRandomly(
                    List<ConditionalInputValue<CategoricalValue>> conditionalInputs) {
                throw new RuntimeException(new OperationNotSupportedException("The method is not implemented yet."));
            }

        };
    }

    public static List<InputValue<CategoricalValue>> toInputs(Object sample) {
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

    private EnvironmentalState<List<InputValue<CategoricalValue>>> asPcmEnvironmentalState(
            List<InputValue<CategoricalValue>> sample) {
        // return EnvironmentalState.get(asPerceivedValue(sample));
        ArrayList<PcmModelChange<List<InputValue<CategoricalValue>>>> attrChanges = new ArrayList<>();
        attrChanges.add(attrChange);
        attrChanges.add(attrChangeServerNode1);
        attrChanges.add(attrChangeServerNode2);
        return new PcmEnvironmentalState<>(attrChanges, asPerceivedValue(sample));
    }

    private PerceivedValue<List<InputValue<CategoricalValue>>> asPerceivedValue(
            List<InputValue<CategoricalValue>> sample) {
        Map<String, String> attributeMap = Maps.newHashMap();
        attributeMap.put(PCM_SPECIFICATION_ATTRIBUTE, WORKLOAD_VARIABLE);
        attributeMap.put(PCM_RESOURCE_CONTAINER_SERVER_1_ATTRIBUTE, SERVER_NODE_1_VARIABLE);
        attributeMap.put(PCM_RESOURCE_CONTAINER_SERVER_2_ATTRIBUTE, SERVER_NODE_2_VARIABLE);

        PerceivedSelectedInputValues perceivedValue = new PerceivedSelectedInputValues(sample, attributeMap);
        return perceivedValue;
    }
}
