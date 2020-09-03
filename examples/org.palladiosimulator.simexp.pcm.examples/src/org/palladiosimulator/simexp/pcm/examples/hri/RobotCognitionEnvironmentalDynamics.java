package org.palladiosimulator.simexp.pcm.examples.hri;

import static java.util.stream.Collectors.toList;
import static org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork.asConditionals;
import static org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork.toConditionalInputs;

import java.util.List;
import java.util.Optional;

import javax.naming.OperationNotSupportedException;

import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork.InputValue;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork.ConditionalInputValue;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork.Trajectory;
import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.environmentaldynamics.entity.CategoricalValue;
import org.palladiosimulator.simexp.environmentaldynamics.entity.DerivableEnvironmentalDynamic;
import org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalState;
import org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalStateObservation;
import org.palladiosimulator.simexp.environmentaldynamics.entity.HiddenEnvironmentalState;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedValue;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.environmentaldynamics.process.UnobservableEnvironmentProcess;
import org.palladiosimulator.simexp.markovian.activity.ObservationProducer;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Observation;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;

import com.google.common.collect.Lists;

public class RobotCognitionEnvironmentalDynamics {

	private static RobotCognitionEnvironmentalDynamics processInstance = null;

	private final EnvironmentProcess envProcess;

	private RobotCognitionEnvironmentalDynamics(DynamicBayesianNetwork dbn) {
		this.envProcess = createEnvironmentalProcess(dbn);
	}

	public static EnvironmentProcess get(DynamicBayesianNetwork dbn) {
		if (processInstance == null) {
			processInstance = new RobotCognitionEnvironmentalDynamics(dbn);
		}
		return processInstance.envProcess;
	}

	private ProbabilityMassFunction createInitialDist(DynamicBayesianNetwork dbn) {
		return new ProbabilityMassFunction() {

			private final BayesianNetwork bn = dbn.getBayesianNetwork();

			@Override
			public Sample drawSample() {
				var trueState = EnvironmentalState.get(new CategoricalValue("MLPrediction", "Unknown"));
				return HiddenEnvironmentalState.get(trueState);

				List<InputValue> sample = bn.sample();
				return Sample.of(asEnvironmentalState(sample), bn.probability(sample));
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

	private EnvironmentProcess createEnvironmentalProcess(DynamicBayesianNetwork dbn) {
		return new UnobservableEnvironmentProcess(createDerivableProcess(), createInitialDist(dbn),
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
			public HiddenEnvironmentalState navigate(NavigationContext context) {
				// Since the intention is to not predict belief states, it is not necessary to know/specify the true state.
				return (HiddenEnvironmentalState) context.getSource();
			}
		};
	}

	private ObservationProducer createObsProducer(DynamicBayesianNetwork dbn) {
		return new ObservationProducer() {

			@Override
			public Observation<?> produceObservationGiven(State emittingState) {
				EnvironmentalState envState = EnvironmentalState.class.cast(context.getSource());
				List<InputValue> inputs = toInputs(envState.getValue().getValue());
				return sample(toConditionalInputs(inputs));
			}

			private EnvironmentalStateObservation sample(List<ConditionalInputValue> conditionalInputs) {
				Trajectory traj = dbn.given(asConditionals(conditionalInputs)).sample();
				return toEnvironmentalStateObservation(traj.valueAtTime(0));
			}
		};
	}

	private EnvironmentalStateObservation toEnvironmentalStateObservation(List<InputValue> sample) {
		var trueState = EnvironmentalState.get(asPreceivedValue(sample));
		return HiddenEnvironmentalState.get(trueState);
	}

	private PerceivedValue<?> asPreceivedValue(List<InputValue> sample) {
		return new PerceivedValue<List<InputValue>>() {

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
					return inputs.stream().map(InputValue.class::cast).collect(toList());
				}
			}
		}
		return Lists.newArrayList();
	}
}
