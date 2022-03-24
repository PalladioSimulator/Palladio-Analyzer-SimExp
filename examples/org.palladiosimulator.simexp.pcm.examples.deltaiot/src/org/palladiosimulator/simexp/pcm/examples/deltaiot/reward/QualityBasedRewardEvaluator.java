package org.palladiosimulator.simexp.pcm.examples.deltaiot.reward;

import static java.util.Objects.requireNonNull;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.LOWER_ENERGY_CONSUMPTION;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.LOWER_PACKET_LOSS;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.PRISM_ENERGY_CONSUMPTION_PROPERTY;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.PRISM_PACKET_LOSS_PROPERTY;

import java.util.List;
import java.util.Optional;

import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.state.StateQuantity;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.RewardImpl;

public class QualityBasedRewardEvaluator implements RewardEvaluator {

	public static class NaturalNumberReward extends RewardImpl<Integer> {
		
		private NaturalNumberReward(int value) {
			super.setValue(value);
		}
		
		public static NaturalNumberReward of(int value) {
			return new NaturalNumberReward(value);
		}
		
		@Override
		public String toString() {
			return Integer.toString(getValue());
		}
		
	}
	
	private final SimulatedMeasurementSpecification packetLossSpec;
	private final SimulatedMeasurementSpecification energyConsumptionSpec;
	
	private QualityBasedRewardEvaluator(SimulatedMeasurementSpecification packetLossSpec,
			SimulatedMeasurementSpecification energyConsumptionSpec) {
		this.packetLossSpec = packetLossSpec;
		this.energyConsumptionSpec = energyConsumptionSpec;
	}
	
	public static QualityBasedRewardEvaluator evaluateBy(List<? extends SimulatedMeasurementSpecification> specs) {
		requireNonNull(specs, "The measurement specs must not be null");
		if (specs.isEmpty()) {
			throw new IllegalArgumentException("The measurement specs must not be empty.");
		}
		
		var packetLossSpec = getSpecWith(PRISM_PACKET_LOSS_PROPERTY, specs)
				.orElseThrow(() -> new IllegalArgumentException("Missing spec: " + PRISM_PACKET_LOSS_PROPERTY));
		var energyConsumptionSpec = getSpecWith(PRISM_ENERGY_CONSUMPTION_PROPERTY, specs)
				.orElseThrow(() -> new IllegalArgumentException("Missing spec: " + PRISM_ENERGY_CONSUMPTION_PROPERTY));
		
		return new QualityBasedRewardEvaluator(packetLossSpec, energyConsumptionSpec);
	}
	
	private static Optional<SimulatedMeasurementSpecification> getSpecWith(String id, 
			List<? extends SimulatedMeasurementSpecification> specs) {
		return specs.stream()
				.map(SimulatedMeasurementSpecification.class::cast)
				.filter(each -> each.getId().equals(id))
				.findFirst();
	}
	
	@Override
	public Reward<?> evaluate(StateQuantity quantifiedState) {
		int value = 0;
		
		var packetLoss = quantifiedState.findMeasurementWith(packetLossSpec).orElseThrow();
		if (LOWER_PACKET_LOSS.isSatisfied(packetLoss.getValue())) {
			value += 1;
		}
		
		var energyConsumption = quantifiedState.findMeasurementWith(energyConsumptionSpec).orElseThrow();
		if (LOWER_ENERGY_CONSUMPTION.isSatisfied(energyConsumption.getValue())) {
			value += 1;
		}
		
		return NaturalNumberReward.of(value);
	}

}
