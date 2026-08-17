package org.palladiosimulator.simexp.pcm.examples.deltaiot.reward;

import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.LOWER_BOUND_ENERGY_CONSUMPTION;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.LOWER_BOUND_PACKET_LOSS;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.UPPER_BOUND_ENERGY_CONSUMPTION;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.UPPER_BOUND_PACKET_LOSS;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.state.StateQuantity;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.RewardImpl;

public class QualityBasedRewardEvaluator implements RewardEvaluator<Double> {
    private static final Logger LOGGER = Logger.getLogger(QualityBasedRewardEvaluator.class);

    public static class RealValuedReward extends RewardImpl<Double> {

        public RealValuedReward(double value) {
            super.setValue(value);
        }

        @Override
        public String toString() {
            return Double.toString(getValue());
        }
    }

    private final SimulatedMeasurementSpecification packetLossSpec;
    private final SimulatedMeasurementSpecification energyConsumptionSpec;

    public QualityBasedRewardEvaluator(SimulatedMeasurementSpecification packetLossSpec,
            SimulatedMeasurementSpecification energyConsumptionSpec) {
        this.packetLossSpec = packetLossSpec;
        this.energyConsumptionSpec = energyConsumptionSpec;
    }

    @Override
    public Reward<Double> evaluate(StateQuantity quantifiedState) {
        SimulatedMeasurement packetLoss = quantifiedState.findMeasurementWith(packetLossSpec)
            .orElseThrow();
        double normalizedPacketLoss = normalizePacketLoss(packetLoss.getValue());
        SimulatedMeasurement energyConsumption = quantifiedState.findMeasurementWith(energyConsumptionSpec)
            .orElseThrow();
        double normalizedEnergyConsumption = normalizeEnergyConsumption(energyConsumption.getValue());

        double normalizedValue = normalizedPacketLoss + normalizedEnergyConsumption;

        Reward<Double> reward = new RealValuedReward(normalizedValue);
        return reward;
    }

    private double normalizeEnergyConsumption(double ec) {
        return normalize(ec, LOWER_BOUND_ENERGY_CONSUMPTION, UPPER_BOUND_ENERGY_CONSUMPTION, "energy consumption");
    }

    private double normalizePacketLoss(double pl) {
        return normalize(pl, LOWER_BOUND_PACKET_LOSS, UPPER_BOUND_PACKET_LOSS, "packet loss");
    }

    double normalize(double value, double lower, double upper, String name) {
        if (value > upper) {
            LOGGER.error(String.format("%s value out of bounds (%f,%f): %f", name, lower, upper, value));
            return 0;
        }

        if (value < lower) {
            LOGGER.error(String.format("%s value out of bounds (%f,%f): %f", name, lower, upper, value));
            return 1;
        }

        return (1 / (upper - lower)) * (upper - value);
    }

}
