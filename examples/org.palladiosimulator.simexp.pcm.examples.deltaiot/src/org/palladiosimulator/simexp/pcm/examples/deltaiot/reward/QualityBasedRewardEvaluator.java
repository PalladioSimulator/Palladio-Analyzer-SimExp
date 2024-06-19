package org.palladiosimulator.simexp.pcm.examples.deltaiot.reward;

import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.LOWER_BOUND_ENERGY_CONSUMPTION;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.LOWER_BOUND_PACKET_LOSS;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.UPPER_BOUND_ENERGY_CONSUMPTION;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons.UPPER_BOUND_PACKET_LOSS;

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

    public QualityBasedRewardEvaluator(SimulatedMeasurementSpecification packetLossSpec,
            SimulatedMeasurementSpecification energyConsumptionSpec) {
        this.packetLossSpec = packetLossSpec;
        this.energyConsumptionSpec = energyConsumptionSpec;
    }

    @Override
    public Reward<Double> evaluate(StateQuantity quantifiedState) {
        double value = 0;

        var packetLoss = quantifiedState.findMeasurementWith(packetLossSpec)
            .orElseThrow();
        value += normalizePacketLoss(packetLoss.getValue());

        var energyConsumption = quantifiedState.findMeasurementWith(energyConsumptionSpec)
            .orElseThrow();
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

        return (1 / (UPPER_BOUND_ENERGY_CONSUMPTION - LOWER_BOUND_ENERGY_CONSUMPTION))
                * (UPPER_BOUND_ENERGY_CONSUMPTION - ec);
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
