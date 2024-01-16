package org.palladiosimulator.simexp.pcm.examples.hri;

import java.util.List;

import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.reward.SimpleRewardSignal;
import org.palladiosimulator.simexp.core.reward.ThresholdBasedRewardEvaluator;
import org.palladiosimulator.simexp.core.state.StateQuantity;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.RewardImpl;

import com.google.common.collect.Lists;

public class RobotCognitionRewardEvaluation extends ThresholdBasedRewardEvaluator {

    private static class NeutralRewardSignal extends RewardImpl<Integer> {

        private NeutralRewardSignal() {
            super.setValue(0);
        }

        public static NeutralRewardSignal create() {
            return new NeutralRewardSignal();
        }

        @Override
        public String toString() {
            return Integer.toString(getValue());
        }
    }

    private final Pair<SimulatedMeasurementSpecification, Threshold> responseTimeThreshold;
    private final Pair<SimulatedMeasurementSpecification, Threshold> reliabilityThreshold;

    public RobotCognitionRewardEvaluation(Pair<SimulatedMeasurementSpecification, Threshold> responseTimeThreshold,
            Pair<SimulatedMeasurementSpecification, Threshold> reliabilityThreshold) {
        super(Lists.newArrayList(responseTimeThreshold, reliabilityThreshold));

        this.responseTimeThreshold = responseTimeThreshold;
        this.reliabilityThreshold = reliabilityThreshold;
    }

    @Override
    public Reward<Integer> evaluate(StateQuantity quantity) {
        var thresholds = filterThresholds(quantity);

        var rtThreshold = findThreshold(responseTimeThreshold.getFirst(), thresholds);
        boolean isRTSatisfied = rtThreshold.getSecond()
            .isSatisfied(rtThreshold.getFirst()
                .getValue());

        var relThreshold = findThreshold(reliabilityThreshold.getFirst(), thresholds);
        boolean isRelSatisfied = relThreshold.getSecond()
            .isSatisfied(relThreshold.getFirst()
                .getValue());

        if (Boolean.logicalAnd(isRTSatisfied, isRelSatisfied)) {
            return SimpleRewardSignal.createPositivReward();
        } else if (Boolean.logicalXor(isRTSatisfied, isRelSatisfied)) {
            return NeutralRewardSignal.create();
        } else {
            return SimpleRewardSignal.createNegativReward();
        }

    }

    private Pair<SimulatedMeasurement, Threshold> findThreshold(SimulatedMeasurementSpecification spec,
            List<Pair<SimulatedMeasurement, Threshold>> thresholds) {
        return thresholds.stream()
            .filter(t -> t.getFirst()
                .getSpecification()
                .getId()
                .equals(spec.getId()))
            .findFirst()
            .get();
    }

}
