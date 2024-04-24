package org.palladiosimulator.simexp.pcm.reliability;

import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.state.StateQuantity;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.RewardImpl;

public class RealValuedRewardEvaluator implements RewardEvaluator<Double> {
    private final SimulatedMeasurementSpecification reliabilitySpec;

    public RealValuedRewardEvaluator(SimulatedMeasurementSpecification reliabilitySpec) {
        this.reliabilitySpec = reliabilitySpec;
    }

    @Override
    public Reward<Double> evaluate(StateQuantity quantifiedState) {
        double reliability = quantifiedState.findMeasurementWith(reliabilitySpec)
            .get()
            .getValue();
        return new RealValuedReward(reliability);
    }

    private static class RealValuedReward extends RewardImpl<Double> {
        public RealValuedReward(double value) {
            super.setValue(value);
        }

        @Override
        public String toString() {
            return Double.toString(getValue());
        }
    }
}
