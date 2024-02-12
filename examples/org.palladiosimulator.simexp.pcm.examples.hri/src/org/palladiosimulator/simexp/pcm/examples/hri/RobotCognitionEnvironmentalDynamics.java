package org.palladiosimulator.simexp.pcm.examples.hri;

import static java.util.stream.Collectors.toList;

import java.util.List;

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

import com.google.common.collect.Lists;

public class RobotCognitionEnvironmentalDynamics<A, R> {

    private final EnvironmentProcess<A, R, List<InputValue>> envProcess;
    private final ConditionalInputValueUtil conditionalInputValueUtil = new ConditionalInputValueUtil();

    public RobotCognitionEnvironmentalDynamics(DynamicBayesianNetwork dbn) {
        this.envProcess = createEnvironmentalProcess(dbn);
    }

    public EnvironmentProcess<A, R, List<InputValue>> getEnvironmentProcess() {
        return envProcess;
    }

    private ProbabilityMassFunction<State> createInitialDist() {
        return new ProbabilityMassFunction<>() {

            @Override
            public Sample<State> drawSample() {
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

    private EnvironmentProcess<A, R, List<InputValue>> createEnvironmentalProcess(DynamicBayesianNetwork dbn) {
        return new UnobservableEnvironmentProcess<>(createDerivableProcess(), createInitialDist(),
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
            public EnvironmentalState<List<InputValue>> navigate(NavigationContext<A> context) {
                // Since the intention is to not predict belief states, it is not necessary to
                // know/specify the true state.
                EnvironmentalState<List<InputValue>> state = (EnvironmentalState<List<InputValue>>) context.getSource();
                if (state.isInitial()) {
                    return EnvironmentalState.<List<InputValue>> newBuilder()
                        .withValue(state.getValue())
                        .isHidden()
                        .build();
                }
                return state;
            }
        };
    }

    private ObservationProducer createObsProducer(DynamicBayesianNetwork dbn) {
        return new ObservationProducer() {

            private EnvironmentalStateObservation<List<InputValue>> last = null;

            @Override
            public Observation produceObservationGiven(State emittingState) {
                EnvironmentalState<List<InputValue>> hiddenState = EnvironmentalState.class.cast(emittingState);

                List<InputValue> sample;
                if (hiddenState.isInitial()) {
                    sample = sampleInitially();
                } else {
                    var inputs = toInputs(last.getValue()
                        .getValue());
                    sample = sampleNext(conditionalInputValueUtil.toConditionalInputs(inputs));
                }

                EnvironmentalStateObservation<List<InputValue>> current = EnvironmentalStateObservation
                    .of(toPerceivedValue(sample), hiddenState);
                setLastEnvironmentalStateObservation(current);

                return current;
            }

            private List<InputValue> sampleInitially() {
                return dbn.getBayesianNetwork()
                    .sample();
            }

            private List<InputValue> sampleNext(List<ConditionalInputValue> conditionalInputs) {
                Trajectory traj = dbn.given(conditionalInputValueUtil.asConditionals(conditionalInputs))
                    .sample();
                return traj.valueAtTime(0);
            }

            private void setLastEnvironmentalStateObservation(EnvironmentalStateObservation<List<InputValue>> current) {
                this.last = current;
            }
        };
    }

    private PerceivedValue<List<InputValue>> toPerceivedValue(List<InputValue> sample) {
        PerceivedInputValues perceivedValue = new PerceivedInputValues(sample);
        return perceivedValue;

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
}
