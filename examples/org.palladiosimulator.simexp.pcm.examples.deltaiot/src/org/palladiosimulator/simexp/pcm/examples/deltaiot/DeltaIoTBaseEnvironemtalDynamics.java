package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Predicate;

import javax.naming.OperationNotSupportedException;

import org.apache.log4j.Logger;
import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.ConditionalInputValueUtil;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork.ConditionalInputValue;
import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundRandomVariable;
import org.palladiosimulator.simexp.core.state.ArchitecturalConfiguration;
import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.environmentaldynamics.entity.DerivableEnvironmentalDynamic;
import org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalState;
import org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalState.EnvironmentalStateBuilder;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedInputValues;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedValue;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.environmentaldynamics.process.ObservableEnvironmentProcess;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator.NavigationContext;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTModelAccess;
import org.palladiosimulator.simexp.pcm.state.PcmArchitecturalConfiguration;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

import com.google.common.collect.Lists;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public abstract class DeltaIoTBaseEnvironemtalDynamics<R> {

    private static final Logger LOGGER = Logger.getLogger(DeltaIoTBaseEnvironemtalDynamics.class.getName());
    private final static String SNR_TEMPLATE = "SignalToNoiseRatio";
    private final static String MA_TEMPLATE = "MoteActivation";

    protected final EnvironmentProcess<QVTOReconfigurator, R, List<InputValue<CategoricalValue>>> envProcess;
    protected final DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess;
    private final ConditionalInputValueUtil<CategoricalValue> conditionalInputValueUtil = new ConditionalInputValueUtil<>();

    public DeltaIoTBaseEnvironemtalDynamics(DynamicBayesianNetwork<CategoricalValue> dbn,
            DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess) {
        this.envProcess = createEnvironmentalProcess(dbn);
        this.modelAccess = modelAccess;
    }

    private EnvironmentProcess<QVTOReconfigurator, R, List<InputValue<CategoricalValue>>> createEnvironmentalProcess(
            DynamicBayesianNetwork<CategoricalValue> dbn) {
        DeltaIoTSampleLogger deltaIoTSampleLogger = new DeltaIoTSampleLogger(modelAccess);
        return new ObservableEnvironmentProcess<QVTOReconfigurator, QVToReconfiguration, R, List<InputValue<CategoricalValue>>>(
                createDerivableProcess(dbn), deltaIoTSampleLogger, createInitialDist(dbn));
    }

    private DerivableEnvironmentalDynamic<QVTOReconfigurator> createDerivableProcess(
            DynamicBayesianNetwork<CategoricalValue> dbn) {
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
            public EnvironmentalState<List<InputValue<CategoricalValue>>> navigate(
                    NavigationContext<QVTOReconfigurator> context) {
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
                var traj = dbn.given(conditionalInputValueUtil.asConditionals(conditionalInputs))
                    .sample();
                var value = toPerceivedValue(traj.valueAtTime(0));
                EnvironmentalStateBuilder<List<InputValue<CategoricalValue>>> builder = EnvironmentalState.newBuilder();
                return builder.withValue(value)
                    .build();
            }

            private EnvironmentalState<List<InputValue<CategoricalValue>>> sampleRandomly(
                    List<ConditionalInputValue<CategoricalValue>> conditionalInputs) {
                throw new RuntimeException(new OperationNotSupportedException("The method is not implemented yet."));
            }
        };
    }

    private ProbabilityMassFunction<State> createInitialDist(DynamicBayesianNetwork<CategoricalValue> dbn) {
        return new ProbabilityMassFunction<>() {

            private final BayesianNetwork<CategoricalValue> bn = dbn.getBayesianNetwork();

            @Override
            public Sample<State> drawSample() {
                var sample = bn.sample();
                EnvironmentalStateBuilder<List<InputValue<CategoricalValue>>> builder = EnvironmentalState.newBuilder();
                State newState = builder.withValue(toPerceivedValue(sample))
                    .isInital()
                    .build();
                return Sample.of(newState, bn.probability(sample));
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

    protected PerceivedValue<List<InputValue<CategoricalValue>>> toPerceivedValue(
            List<InputValue<CategoricalValue>> sample) {
        PerceivedInputValues perceivedValue = new PerceivedInputValues(sample);
        return perceivedValue;
    }

    public static <A> PcmSelfAdaptiveSystemState<A, List<InputValue<CategoricalValue>>> asPcmState(State state) {
        return PcmSelfAdaptiveSystemState.class.cast(state);
    }

    public static <A> PerceivableEnvironmentalState<List<InputValue<CategoricalValue>>> getCurrentEnvironment(
            NavigationContext<A> context) {
        return asPcmState(context.getSource()).getPerceivedEnvironmentalState();
    }

    public static <A> PcmArchitecturalConfiguration<A> getCurrentArchitecture(NavigationContext<A> context) {
        PcmSelfAdaptiveSystemState<A, List<InputValue<CategoricalValue>>> pcmState = asPcmState(context.getSource());
        ArchitecturalConfiguration<PCMInstance, A> pcmConfig = pcmState.getArchitecturalConfiguration();
        return PcmArchitecturalConfiguration.class.cast(pcmConfig);
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

    protected static Predicate<GroundRandomVariable> isWITemplate() {
        return isMATemplate().or(v -> isSNRTemplate(v))
            .negate();
    }

    protected static Predicate<GroundRandomVariable> isMATemplate() {
        return v -> v.getInstantiatedTemplate()
            .getEntityName()
            .equals(MA_TEMPLATE);
    }

    public static boolean isSNRTemplate(GroundRandomVariable variable) {
        return variable.getInstantiatedTemplate()
            .getEntityName()
            .equals(SNR_TEMPLATE);
    }
}
