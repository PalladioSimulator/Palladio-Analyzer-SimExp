package org.palladiosimulator.simexp.pcm.examples.hri;

import static java.util.stream.Collectors.toList;
import static org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork.asConditionals;
import static org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork.toConditionalInputs;

import java.util.List;
import java.util.Optional;

import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork.InputValue;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork.ConditionalInputValue;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork.Trajectory;
import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.environmentaldynamics.entity.CategoricalValue;
import org.palladiosimulator.simexp.environmentaldynamics.entity.DerivableEnvironmentalDynamic;
import org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalState;
import org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalState.EnvironmentalStateBuilder;
import org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalStateObservation;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedValue;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.environmentaldynamics.process.UnobservableEnvironmentProcess;
import org.palladiosimulator.simexp.markovian.activity.ObservationProducer;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Observation;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

import com.google.common.collect.Lists;

public class RobotCognitionEnvironmentalDynamics<S, A, R> {

    private final EnvironmentProcess<S, A, R> envProcess;

    public RobotCognitionEnvironmentalDynamics(DynamicBayesianNetwork dbn) {
        this.envProcess = createEnvironmentalProcess(dbn);
    }

    public EnvironmentProcess<S, A, R> getEnvironmentProcess() {
        return envProcess;
    }

    private ProbabilityMassFunction<State<S>> createInitialDist() {
        return new ProbabilityMassFunction<>() {

            @Override
            public Sample<State<S>> drawSample() {
                EnvironmentalStateBuilder<S, String> builder = EnvironmentalState.newBuilder();
                EnvironmentalState<S, String> initial = builder
                    .withValue(new CategoricalValue("MLPrediction", "Unknown"))
                    .isInital()
                    .isHidden()
                    .build();
                // Sample<EnvironmentalState<S, String>> sample = Sample.of(initial, 1.0);
                Sample<State<S>> sample = Sample.of((State<S>) initial, 1.0);
                return sample;
            }

            @Override
            public double probability(Sample<State<S>> sample) {
                return 1.0;
            }
        };
    }

    private EnvironmentProcess<S, A, R> createEnvironmentalProcess(DynamicBayesianNetwork dbn) {
        return new UnobservableEnvironmentProcess(createDerivableProcess(), createInitialDist(),
                createObsProducer(dbn));
    }

    private DerivableEnvironmentalDynamic createDerivableProcess() {
        return new DerivableEnvironmentalDynamic() {

            @Override
            public void pursueExplorationStrategy() {

            }

            @Override
            public void pursueExploitationStrategy() {

            }

            @Override
            public EnvironmentalState navigate(NavigationContext context) {
                // Since the intention is to not predict belief states, it is not necessary to
                // know/specify the true state.
                var state = (EnvironmentalState) context.getSource();
                if (state.isInitial()) {
                    return EnvironmentalState.newBuilder()
                        .withValue(state.getValue())
                        .isHidden()
                        .build();
                }
                return state;
            }
        };
    }

    private ObservationProducer<S> createObsProducer(DynamicBayesianNetwork dbn) {
        return new ObservationProducer<>() {

            private EnvironmentalStateObservation last = null;

            @Override
            public Observation<S> produceObservationGiven(State<S> emittingState) {
                var hiddenState = EnvironmentalState.class.cast(emittingState);

                List<InputValue> sample;
                if (hiddenState.isInitial()) {
                    sample = sampleInitially();
                } else {
                    var inputs = toInputs(last.getValue()
                        .getValue());
                    sample = sampleNext(toConditionalInputs(inputs));
                }

                var current = EnvironmentalStateObservation.of(toPerceivedValue(sample), hiddenState);
                setLastEnvironmentalStateObservation(current);

                return current;
            }

            private List<InputValue> sampleInitially() {
                return dbn.getBayesianNetwork()
                    .sample();
            }

            private List<InputValue> sampleNext(List<ConditionalInputValue> conditionalInputs) {
                Trajectory traj = dbn.given(asConditionals(conditionalInputs))
                    .sample();
                return traj.valueAtTime(0);
            }

            private void setLastEnvironmentalStateObservation(EnvironmentalStateObservation current) {
                this.last = current;
            }
        };
    }

    private PerceivedValue<List<InputValue>> toPerceivedValue(List<InputValue> sample) {
        return new PerceivedValue<>() {

            @Override
            public List<InputValue> getValue() {
                return sample;
            }

            @Override
            public Optional<Object> getElement(String key) {
                return Optional.of(sample);
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
