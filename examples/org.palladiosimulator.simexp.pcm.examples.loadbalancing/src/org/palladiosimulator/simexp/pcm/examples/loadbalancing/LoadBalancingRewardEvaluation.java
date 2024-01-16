package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

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

public class LoadBalancingRewardEvaluation extends ThresholdBasedRewardEvaluator {

    private final static Threshold TOTAL_UTIL_THRESHOLD = Threshold.lessThanOrEqualTo(1.0);

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

    private final Pair<SimulatedMeasurementSpecification, Threshold> upperResponseTimeThreshold;
    private final Pair<SimulatedMeasurementSpecification, Threshold> cpuServer1Threshold;
    private final Pair<SimulatedMeasurementSpecification, Threshold> cpuServer2Threshold;

    public LoadBalancingRewardEvaluation(Pair<SimulatedMeasurementSpecification, Threshold> upperResponseTimeThreshold,
            Pair<SimulatedMeasurementSpecification, Threshold> cpuServer1Threshold,
            Pair<SimulatedMeasurementSpecification, Threshold> cpuServer2Threshold) {
        super(Lists.newArrayList(upperResponseTimeThreshold, cpuServer1Threshold, cpuServer2Threshold));

        this.upperResponseTimeThreshold = upperResponseTimeThreshold;
        this.cpuServer1Threshold = cpuServer1Threshold;
        this.cpuServer2Threshold = cpuServer2Threshold;
    }

    @Override
    public Reward<Integer> evaluate(StateQuantity quantity) {
        List<Pair<SimulatedMeasurement, Threshold>> thresholds = filterThresholds(quantity);
        Pair<SimulatedMeasurement, Threshold> rtThreshold = findThreshold(upperResponseTimeThreshold.getFirst(),
                thresholds);
        boolean isRTSatisfied = rtThreshold.getSecond()
            .isSatisfied(rtThreshold.getFirst()
                .getValue());

        SimulatedMeasurement utilServer1 = findThreshold(cpuServer1Threshold.getFirst(), thresholds).getFirst();
        SimulatedMeasurement utilServer2 = findThreshold(cpuServer2Threshold.getFirst(), thresholds).getFirst();
        Double totalUtil = utilServer1.getValue() + utilServer2.getValue();
        boolean isUtilSatisfied = TOTAL_UTIL_THRESHOLD.isSatisfied(totalUtil);

        if (Boolean.logicalAnd(isRTSatisfied, isUtilSatisfied)) {
            return SimpleRewardSignal.createPositivReward();
        } else if (Boolean.logicalXor(isRTSatisfied, isUtilSatisfied)) {
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
