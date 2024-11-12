package org.palladiosimulator.simexp.pcm.performance;

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
import org.palladiosimulator.simexp.markovian.sampling.SampleDumper;
import org.palladiosimulator.simexp.pcm.perceiption.PcmAttributeChange;
import org.palladiosimulator.simexp.pcm.perceiption.PcmEnvironmentalState;
import org.palladiosimulator.simexp.pcm.perceiption.PcmModelChange;
import org.palladiosimulator.simexp.pcm.perceiption.PerceivedValueConverter;
import org.palladiosimulator.simexp.pcm.util.ExperimentRunner;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.uka.ipd.sdq.probfunction.math.IContinousPDF;
import de.uka.ipd.sdq.probfunction.math.apache.impl.PDFFactory;
import de.uka.ipd.sdq.stoex.StoexPackage;
import tools.mdsd.probdist.api.entity.CategoricalValue;

public class PerformanceVaryingInterarrivelRateProcess<A, Aa extends Action<A>, R> {

    private static final Logger LOGGER = Logger.getLogger(PerformanceVaryingInterarrivelRateProcess.class.getName());

    public final static String WORKLOAD_VARIABLE = "WorkloadVariation_VaryingWorkloadInstantiation";

    private final static double RATE = 1.5;
    private final static double UPPER_PROB_BOUND = 0.999;
    private final static double INTERARRIVAL_TIME_UPPER_BOUND = 0.15;
    private final static int NUMBER_OF_STATES = 100;
    private final static IContinousPDF INTERARRIVAL_RATE_DISTRIBUTION = new PDFFactory()
        .createExponentialDistribution(RATE);
    private final static String PCM_SPECIFICATION_ATTRIBUTE = StoexPackage.Literals.RANDOM_VARIABLE__SPECIFICATION
        .getName();

    // private final static String PCM_RESOURCE_CONTAINER_SERVER_1_ATTRIBUTE = "ServerNode1"; //
    // entityName pcm resource container representing server node 1
    // private final static String SERVER_NODE_1_VARIABLE =
    // "ServerNode1Failure_ServerFailureInstantiation";
    // private final static String PCM_RESOURCE_CONTAINER_SERVER_2_ATTRIBUTE = "ServerNode2"; //
    // entityName pcm resource container representing server node 2
    // private final static String SERVER_NODE_2_VARIABLE =
    // "ServerNode2Failure_ServerFailureInstantiation";

    private final PcmAttributeChange<List<InputValue<CategoricalValue>>> attrChange;
    // private static PcmModelChange attrChangeServerNode1;
    // private static PcmModelChange attrChangeServerNode2;
    private final EnvironmentProcess<A, R, List<InputValue<CategoricalValue>>> envProcess;
    private final ProbabilityMassFunction<State> initialDist;
    private final ConditionalInputValueUtil<CategoricalValue> conditionalInputValueUtil = new ConditionalInputValueUtil<>();

    private SampleDumper sampleDumper = null;

    public PerformanceVaryingInterarrivelRateProcess(DynamicBayesianNetwork<CategoricalValue> dbn,
            IExperimentProvider experimentProvider) {
        // initPcmAttributeChange(experimentProvider);
        PerceivedValueConverter<List<InputValue<CategoricalValue>>> perceivedValueConverter = new PerceivedValueConverter<>() {

            @Override
            public CategoricalValue convertElement(PerceivedValue<List<InputValue<CategoricalValue>>> change,
                    String key) {
                PerceivedElement<List<InputValue<CategoricalValue>>> pe = (PerceivedElement<List<InputValue<CategoricalValue>>>) change;
                Optional<List<InputValue<CategoricalValue>>> values = pe.getElement(key);
                List<InputValue<CategoricalValue>> valueList = values.get();
                InputValue<CategoricalValue> value = valueList.get(0);
                CategoricalValue changedValue = value.getValue();
                return changedValue;
            }
        };

        this.attrChange = new PcmAttributeChange<>(retrieveInterArrivalTimeRandomVariableHandler(),
                PCM_SPECIFICATION_ATTRIBUTE, experimentProvider, perceivedValueConverter);
        this.initialDist = createInitialDist(dbn);
        this.envProcess = createEnvironmentalProcess(dbn);
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

    public EnvironmentProcess<A, R, List<InputValue<CategoricalValue>>> getEnvironmentProcess() {
        return envProcess;
    }

    private EnvironmentProcess<A, R, List<InputValue<CategoricalValue>>> createEnvironmentalProcess(
            DynamicBayesianNetwork<CategoricalValue> dbn) {
        return new ObservableEnvironmentProcess<A, Aa, R, List<InputValue<CategoricalValue>>>(
                createDerivableProcess(dbn), sampleDumper, initialDist);
    }

    private ProbabilityMassFunction<State> createInitialDist(DynamicBayesianNetwork<CategoricalValue> dbn) {
        return new ProbabilityMassFunction<>() {

            private final BayesianNetwork<CategoricalValue> bn = dbn.getBayesianNetwork();

            private boolean initialized = false;

            @Override
            public void init(int seed) {
                initialized = true;
                bn.init(seed);
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
        // attrChanges.add(attrChangeServerNode1);
        // attrChanges.add(attrChangeServerNode2);
        return new PcmEnvironmentalState<>(attrChanges, asPerceivedValue(sample));
    }

    private PerceivedValue<List<InputValue<CategoricalValue>>> asPerceivedValue(
            List<InputValue<CategoricalValue>> sample) {
        Map<String, String> attributeMap = Maps.newHashMap();
        attributeMap.put(PCM_SPECIFICATION_ATTRIBUTE, WORKLOAD_VARIABLE);
        PerceivedSelectedInputValues perceivedValue = new PerceivedSelectedInputValues(sample, attributeMap);
        return perceivedValue;
    }
}
