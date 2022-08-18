package org.palladiosimulator.simexp.pcm.examples.udacitychallenge.reliability;

import static java.util.stream.Collectors.toList;
import static org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork.asConditionals;
import static org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork.toConditionalInputs;

import java.util.List;
import java.util.Optional;

import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork.InputValue;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork.ConditionalInputValue;
import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.environmentaldynamics.entity.DerivableEnvironmentalDynamic;
import org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalState;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedValue;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.environmentaldynamics.process.ObservableEnvironmentProcess;

import com.google.common.collect.Lists;

public class UdacityEnvironmentalDynamics {

	private static UdacityEnvironmentalDynamics processInstance = null;

	private final EnvironmentProcess envProcess;

	private UdacityEnvironmentalDynamics(DynamicBayesianNetwork dbn) {
		this.envProcess = createEnvironmentalProcess(dbn);
	}

	public static EnvironmentProcess get(DynamicBayesianNetwork dbn) {
		if (processInstance == null) {
			processInstance = new UdacityEnvironmentalDynamics(dbn);
		}
		return processInstance.envProcess;
	}

	private EnvironmentProcess createEnvironmentalProcess(DynamicBayesianNetwork dbn) {
		return new ObservableEnvironmentProcess(createDerivableProcess(dbn), createInitialDist(dbn));
	}

	private DerivableEnvironmentalDynamic createDerivableProcess(DynamicBayesianNetwork dbn) {
		return new DerivableEnvironmentalDynamic() {

			@Override
			public void pursueExplorationStrategy() {

			}

			@Override
			public void pursueExploitationStrategy() {

			}

			@Override
			public EnvironmentalState navigate(NavigationContext context) {
				var envState = (EnvironmentalState) context.getSource();
				var inputs = toInputs(envState.getValue().getValue());
				return sample(toConditionalInputs(inputs));
			}

			private EnvironmentalState sample(List<ConditionalInputValue> conditionalInputs) {
				var traj = dbn.given(asConditionals(conditionalInputs)).sample();
				var value = toPerceivedValue(traj.valueAtTime(0));
				return EnvironmentalState.newBuilder().withValue(value).build();
			}

		};
	}

	private ProbabilityMassFunction createInitialDist(DynamicBayesianNetwork dbn) {
		return new ProbabilityMassFunction() {

			private final BayesianNetwork bn = dbn.getBayesianNetwork();

			@Override
			public Sample drawSample() {
				var sample = bn.sample();
				var value = toPerceivedValue(sample);
				var initial = EnvironmentalState.newBuilder().withValue(value).isInital().build();
				return Sample.of(initial, bn.probability(sample));
			}

			@Override
			public double probability(Sample sample) {
				var inputs = toInputs(sample);
				if (inputs.isEmpty()) {
					return 0;
				}
				return bn.probability(inputs);
			}
		};
	}

	private PerceivedValue<?> toPerceivedValue(List<InputValue> sample) {
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
				var builder = new StringBuilder();
				for (InputValue each : sample) {
					builder.append(String.format("(Variable: %1s, Value: %2s),", each.variable.getEntityName(),
							each.value.toString()));
				}

				var stringValues = builder.toString();
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
