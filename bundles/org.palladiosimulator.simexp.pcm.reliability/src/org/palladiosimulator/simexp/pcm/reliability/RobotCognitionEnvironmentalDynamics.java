package org.palladiosimulator.simexp.pcm.reliability;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import org.palladiosimulator.envdyn.api.entity.bn.ConditionalInputValueUtil;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork.ConditionalInputValue;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork.Trajectory;
import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.environmentaldynamics.entity.DerivableEnvironmentalDynamic;
import org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalState;
import org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalState.EnvironmentalStateBuilder;
import org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalStateObservation;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedCategoricalValue;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedInputValues;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedValue;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.environmentaldynamics.process.UnobservableEnvironmentProcess;
import org.palladiosimulator.simexp.markovian.activity.ObservationProducer;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Observation;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.sampling.SampleDumper;

import com.google.common.collect.Lists;

import tools.mdsd.probdist.api.entity.CategoricalValue;
import tools.mdsd.probdist.api.random.ISeedProvider;
import tools.mdsd.probdist.api.random.ISeedable;

public class RobotCognitionEnvironmentalDynamics<A, R> implements ISeedable {

    private final ProbabilityMassFunction<State> initialDist;
    private final EnvironmentProcess<A, R, List<InputValue<CategoricalValue>>> envProcess;
    private final ConditionalInputValueUtil<CategoricalValue> conditionalInputValueUtil = new ConditionalInputValueUtil<>();

    private SampleDumper sampleDumper = null;
    private boolean initialized = false;

    public RobotCognitionEnvironmentalDynamics(DynamicBayesianNetwork<CategoricalValue> dbn) {
        this.initialDist = createInitialDist();
        this.envProcess = createEnvironmentalProcess(dbn, initialDist);
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

    private ProbabilityMassFunction<State> createInitialDist() {
        return new ProbabilityMassFunction<>() {
            private boolean initialized = false;

            @Override
            public void init(Optional<ISeedProvider> seedProvider) {
                initialized = true;
            }

            @Override
            public Sample<State> drawSample() {
                if (!initialized) {
                    throw new RuntimeException("not initialized");
                }
                EnvironmentalStateBuilder<String> builder = EnvironmentalState.newBuilder();
                EnvironmentalState<String> initial = builder
                    .withValue(new PerceivedCategoricalValue("MLPrediction", "Unknown"))
                    .isInital()
                    .isHidden()
                    .build();
                // Sample<EnvironmentalState<S, String>> sample = Sample.of(initial, 1.0);
                Sample<State> sample = Sample.of((State) initial, 1.0);
                return sample;
            }

            @Override
            public double probability(Sample<State> sample) {
                return 1.0;
            }
        };
    }

    private EnvironmentProcess<A, R, List<InputValue<CategoricalValue>>> createEnvironmentalProcess(
            DynamicBayesianNetwork<CategoricalValue> dbn, ProbabilityMassFunction<State> initialDist) {
        return new UnobservableEnvironmentProcess<>(createDerivableProcess(), sampleDumper, initialDist,
                createObsProducer(dbn));
    }

    private DerivableEnvironmentalDynamic<A> createDerivableProcess() {
        return new DerivableEnvironmentalDynamic<>() {

            @Override
            public void pursueExplorationStrategy() {

            }

            @Override
            public void pursueExploitationStrategy() {

            }

            @Override
            public EnvironmentalState<List<InputValue<CategoricalValue>>> navigate(NavigationContext<A> context) {
                // Since the intention is to not predict belief states, it is not necessary to
                // know/specify the true state.
                EnvironmentalState<List<InputValue<CategoricalValue>>> state = (EnvironmentalState<List<InputValue<CategoricalValue>>>) context
                    .getSource();
                if (state.isInitial()) {
                    return EnvironmentalState.<List<InputValue<CategoricalValue>>> newBuilder()
                        .withValue(state.getValue())
                        .isHidden()
                        .build();
                }
                return state;
            }
        };
    }

    private ObservationProducer createObsProducer(DynamicBayesianNetwork<CategoricalValue> dbn) {
        return new ObservationProducer() {

            private EnvironmentalStateObservation<List<InputValue<CategoricalValue>>> last = null;

            @Override
            public Observation produceObservationGiven(State emittingState) {
                EnvironmentalState<List<InputValue<CategoricalValue>>> hiddenState = EnvironmentalState.class
                    .cast(emittingState);

                List<InputValue<CategoricalValue>> sample;
                if (hiddenState.isInitial()) {
                    sample = sampleInitially();
                } else {
                    var inputs = toInputs(last.getValue()
                        .getValue());
                    sample = sampleNext(conditionalInputValueUtil.toConditionalInputs(inputs));
                }

                EnvironmentalStateObservation<List<InputValue<CategoricalValue>>> current = EnvironmentalStateObservation
                    .of(toPerceivedValue(sample), hiddenState);
                setLastEnvironmentalStateObservation(current);

                return current;
            }

            private List<InputValue<CategoricalValue>> sampleInitially() {
                return dbn.getBayesianNetwork()
                    .sample();
            }

            private List<InputValue<CategoricalValue>> sampleNext(
                    List<ConditionalInputValue<CategoricalValue>> conditionalInputs) {
                Trajectory<CategoricalValue> traj = dbn
                    .given(conditionalInputValueUtil.asConditionals(conditionalInputs))
                    .sample();
                return traj.valueAtTime(0);
            }

            private void setLastEnvironmentalStateObservation(
                    EnvironmentalStateObservation<List<InputValue<CategoricalValue>>> current) {
                this.last = current;
            }
        };
    }

    private PerceivedValue<List<InputValue<CategoricalValue>>> toPerceivedValue(
            List<InputValue<CategoricalValue>> sample) {
        PerceivedInputValues perceivedValue = new PerceivedInputValues(sample);
        return perceivedValue;

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
}
