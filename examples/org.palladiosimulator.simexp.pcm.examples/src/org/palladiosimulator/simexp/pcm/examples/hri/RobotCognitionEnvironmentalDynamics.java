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
import org.palladiosimulator.simexp.environmentaldynamics.entity.DerivableEnvironmentalDynamic;
import org.palladiosimulator.simexp.environmentaldynamics.entity.EnvironmentalState;
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
		return new UnobservableEnvironmentProcess(createDerivableProcess(dbn), createInitialDist(dbn), createObsProducer());
	}

	private DerivableEnvironmentalDynamic createDerivableProcess(DynamicBayesianNetwork dbn) {
		return new DerivableEnvironmentalDynamic() {

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
			public HiddenEnvironmentalState navigate(NavigationContext context) {
				EnvironmentalState envState = EnvironmentalState.class.cast(context.getSource());
				List<InputValue> inputs = toInputs(envState.getValue().getValue());
				if (explorationMode) {
					return sampleRandomly(toConditionalInputs(inputs));
				}
				return sample(toConditionalInputs(inputs));
			}

			private HiddenEnvironmentalState sample(List<ConditionalInputValue> conditionalInputs) {
				Trajectory traj = dbn.given(asConditionals(conditionalInputs)).sample();
				return asEnvironmentalState(traj.valueAtTime(0));
			}

			private HiddenEnvironmentalState sampleRandomly(List<ConditionalInputValue> conditionalInputs) {
				throw new RuntimeException(new OperationNotSupportedException("The method is not implemented yet."));
			}
		};
	}
	
	private ObservationProducer createObsProducer() {
		return new ObservationProducer() {
			
			@Override
			public Observation<?> produceObservationGiven(State emittingState) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	private HiddenEnvironmentalState asEnvironmentalState(List<InputValue> sample) {
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
