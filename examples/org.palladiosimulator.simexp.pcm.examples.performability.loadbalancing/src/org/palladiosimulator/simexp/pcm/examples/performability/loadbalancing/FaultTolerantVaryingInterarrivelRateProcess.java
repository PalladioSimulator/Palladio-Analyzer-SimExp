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

public class FaultTolerantVaryingInterarrivelRateProcess<C, A, Aa extends Action<A>, R> {

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
    private final EnvironmentProcess<A, R, List<InputValue>> envProcess;
    private final ProbabilityMassFunction<State> initialDist;

    public FaultTolerantVaryingInterarrivelRateProcess(DynamicBayesianNetwork dbn,
            IExperimentProvider experimentProvider) {
        PerceivedValueConverter<List<InputValue>> perceivedValueConverter = new PerceivedValueConverter<>() {

            @Override
            public CategoricalValue convertElement(PerceivedValue<List<InputValue>> change, String key) {
                PerceivedElement<List<InputValue>> pe = (PerceivedElement<List<InputValue>>) change;
                Optional<List<InputValue>> values = pe.getElement(key);
                List<InputValue> valueList = values.get();
                InputValue value = valueList.get(0);
                CategoricalValue changedValue = value.asCategorical();
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

    public EnvironmentProcess<A, R, List<InputValue>> getEnvironmentProcess() {
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

    private EnvironmentProcess<A, R, List<InputValue>> createEnvironmentalProcess(DynamicBayesianNetwork dbn) {
        return new ObservableEnvironmentProcess<>(createDerivableProcess(dbn), initialDist);
    }

    private ProbabilityMassFunction<State> createInitialDist(DynamicBayesianNetwork dbn) {
        return new ProbabilityMassFunction<>() {

            private final BayesianNetwork bn = dbn.getBayesianNetwork();

            @Override
            public Sample<State> drawSample() {
                List<InputValue> sample = bn.sample();
                return Sample.of(asPcmEnvironmentalState(sample), bn.probability(sample));
            }

            @Override
            public double probability(Sample<State> sample) {
                List<InputValue> inputs = toInputs(sample);
                if (inputs.isEmpty()) {
                    return 0;
                }
                return bn.probability(inputs);
            }
        };
    }

    private DerivableEnvironmentalDynamic<A> createDerivableProcess(DynamicBayesianNetwork dbn) {
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
            public EnvironmentalState<List<InputValue>> navigate(NavigationContext<A> context) {
                EnvironmentalState<List<InputValue>> envState = EnvironmentalState.class.cast(context.getSource());
                List<InputValue> inputs = toInputs(envState.getValue()
                    .getValue());
                if (explorationMode) {
                    return sampleRandomly(DynamicBayesianNetwork.toConditionalInputs(inputs));
                }
                return sample(DynamicBayesianNetwork.toConditionalInputs(inputs));
            }

            private EnvironmentalState<List<InputValue>> sample(List<ConditionalInputValue> conditionalInputs) {
                Trajectory traj = dbn.given(DynamicBayesianNetwork.asConditionals(conditionalInputs))
                    .sample();
                return asPcmEnvironmentalState(traj.valueAtTime(0));
            }

            private EnvironmentalState<List<InputValue>> sampleRandomly(List<ConditionalInputValue> conditionalInputs) {
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

    private EnvironmentalState<List<InputValue>> asPcmEnvironmentalState(List<InputValue> sample) {
        // return EnvironmentalState.get(asPerceivedValue(sample));
        ArrayList<PcmModelChange<List<InputValue>>> attrChanges = new ArrayList<>();
        attrChanges.add(attrChange);
        attrChanges.add(attrChangeServerNode1);
        attrChanges.add(attrChangeServerNode2);
        return new PcmEnvironmentalState(attrChanges, asPerceivedValue(sample));
    }

    private PerceivedValue<List<InputValue>> asPerceivedValue(List<InputValue> sample) {
        Map<String, String> attributeMap = Maps.newHashMap();
        attributeMap.put(PCM_SPECIFICATION_ATTRIBUTE, WORKLOAD_VARIABLE);
        attributeMap.put(PCM_RESOURCE_CONTAINER_SERVER_1_ATTRIBUTE, SERVER_NODE_1_VARIABLE);
        attributeMap.put(PCM_RESOURCE_CONTAINER_SERVER_2_ATTRIBUTE, SERVER_NODE_2_VARIABLE);

        PerceivedSelectedInputValues perceivedValue = new PerceivedSelectedInputValues(sample, attributeMap);
        return perceivedValue;
    }
}
