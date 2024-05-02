package org.palladiosimulator.simexp.pcm.examples.deltaiot.reward;

import static java.util.Objects.requireNonNull;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.LOWER_BOUND_ENERGY_CONSUMPTION;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.LOWER_BOUND_PACKET_LOSS;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.PRISM_ENERGY_CONSUMPTION_PROPERTY;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.PRISM_PACKET_LOSS_PROPERTY;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.UPPER_BOUND_ENERGY_CONSUMPTION;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.UPPER_BOUND_PACKET_LOSS;

import java.util.List;
import java.util.Optional;

import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.state.StateQuantity;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.RewardImpl;

public class QualityBasedRewardEvaluator implements RewardEvaluator {

	public static class RealValuedReward extends RewardImpl<Double> {

		private RealValuedReward(double value) {
			super.setValue(value);
		}

		public static RealValuedReward of(double value) {
			return new RealValuedReward(value);
		}

		@Override
		public String toString() {
			return Double.toString(getValue());
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
		return specs.stream().map(SimulatedMeasurementSpecification.class::cast).filter(each -> each.getId().equals(id))
				.findFirst();
	}

	@Override
	public Reward<?> evaluate(StateQuantity quantifiedState) {
		double value = 0;

		var packetLoss = quantifiedState.findMeasurementWith(packetLossSpec).orElseThrow();
		value += normalizePacketLoss(packetLoss.getValue());

		var energyConsumption = quantifiedState.findMeasurementWith(energyConsumptionSpec).orElseThrow();
		value += normalizeEnergyConsumption(energyConsumption.getValue());

		return RealValuedReward.of(value);
	}
	
	private double normalizeEnergyConsumption(double ec) {
		if (ec > UPPER_BOUND_ENERGY_CONSUMPTION) {
			return 0;
		}
		
		if (ec < LOWER_BOUND_ENERGY_CONSUMPTION) {
			return 1;
		}

		return (1 / (UPPER_BOUND_ENERGY_CONSUMPTION - LOWER_BOUND_ENERGY_CONSUMPTION)) * (UPPER_BOUND_ENERGY_CONSUMPTION - ec);
	}
	
	private double normalizePacketLoss(double pl) {
		if (pl > UPPER_BOUND_PACKET_LOSS) {
			return 0;
		}
		
		if (pl < LOWER_BOUND_PACKET_LOSS) {
			return 1;
		}
		
		return (1 / (UPPER_BOUND_PACKET_LOSS - LOWER_BOUND_PACKET_LOSS)) * (UPPER_BOUND_PACKET_LOSS - pl);
	}

}
