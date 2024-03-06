package org.palladiosimulator.simexp.pcm.performability;

import java.util.Locale;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.state.StateQuantity;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.RewardImpl;
import org.palladiosimulator.simexp.pcm.examples.performability.ResponseTimeLinearInterpolator;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;

public class PerformabilityRewardEvaluation implements RewardEvaluator<Double> {

    private static final Logger LOGGER = Logger.getLogger(PerformabilityRewardEvaluation.class.getName());

    private final PcmMeasurementSpecification responseTimeMeasurementSpec;
    private final PcmMeasurementSpecification systemResultExectutionTypeTimeMeasurementSpec;
    private Pair<SimulatedMeasurementSpecification, Threshold> lowerResponseTimeThreshold;
    private Pair<SimulatedMeasurementSpecification, Threshold> upperResponseTimeThreshold;

    public PerformabilityRewardEvaluation(PcmMeasurementSpecification responseTimeMeasurementSpec,
            PcmMeasurementSpecification systemResultExectutionTypeTimeMeasurementSpec,
            Pair<SimulatedMeasurementSpecification, Threshold> lowerResponseTimeThreshold,
            Pair<SimulatedMeasurementSpecification, Threshold> upperResponseTimeThreshold) {
        this.responseTimeMeasurementSpec = responseTimeMeasurementSpec;
        this.systemResultExectutionTypeTimeMeasurementSpec = systemResultExectutionTypeTimeMeasurementSpec;
        this.lowerResponseTimeThreshold = lowerResponseTimeThreshold;
        this.upperResponseTimeThreshold = upperResponseTimeThreshold;
    }

    @Override
    public Reward<Double> evaluate(StateQuantity quantity) {
        /**
         * method implements the reward signal based on the simulated measured response times;
         * reward signal will be used by {@link PerformabilityEvaluator.evaluate} to calculate the
         * total reward based on the performability metric
         * 
         */
        LOGGER.debug(String.format("Quantified state: %s", quantity.toString()));
        double lower = lowerResponseTimeThreshold.getSecond()
            .getValue();
        double upper = upperResponseTimeThreshold.getSecond()
            .getValue();
        double measuredResponseTime = 0.0;
        double measuredSuccessRate = 0.0;

        // filter simulated response time measurement from set (key: Usage Scenario:
        // UsageScenario_Response Time)
        Optional<SimulatedMeasurement> simulatedResponseTimeMeasurement = quantity
            .findMeasurementWith(responseTimeMeasurementSpec);
        Optional<SimulatedMeasurement> simulatedSuccessRateTimeMeasurement = quantity
            .findMeasurementWith(systemResultExectutionTypeTimeMeasurementSpec);

        if (!simulatedResponseTimeMeasurement.isPresent()) {
            LOGGER.error(String.format(
                    "No simulated response time measurements available for quantified state: %s; check your measurement specification.",
                    quantity.getMeasurements()
                        .toString()));
        } else {
            LOGGER.info(String.format("Simulated response time measurements available for quantified state %s",
                    quantity.toString()));
            measuredResponseTime = simulatedResponseTimeMeasurement.get()
                .getValue();
        }
        if (!simulatedSuccessRateTimeMeasurement.isPresent()) {
            LOGGER.error(String.format(
                    "No simulated success rate measurements available for quantified state: %s; check your measurement specification.",
                    quantity.getMeasurements()
                        .toString()));
        } else {
            LOGGER.info(String.format("Simulated success rate measurements available for quantified state %s",
                    quantity.toString()));
            measuredSuccessRate = simulatedSuccessRateTimeMeasurement.get()
                .getValue();
        }

        PerformabilityRewardSignal rewardSignal = PerformabilityRewardSignal.create(measuredResponseTime,
                measuredSuccessRate, lower, upper);
        LOGGER.debug(String.format(Locale.ENGLISH, "Performability reward signal for quantified state: %.10f",
                rewardSignal.getValue()));
        return rewardSignal;
    }

    private static class PerformabilityRewardSignal extends RewardImpl<Double> {

        private PerformabilityRewardSignal(double responseTime) {
            super.setValue(responseTime);
        }

        public static PerformabilityRewardSignal create(double responseTime, double successRate,
                double lowerResponseTime, double upperResponseTime) {
            // FIXME: only interpolate if response time is in current range; add range check
            double interpolatedResponseTime = 0.0;
            if (responseTime > lowerResponseTime && responseTime < upperResponseTime) {
                interpolatedResponseTime = ResponseTimeLinearInterpolator.interpolate(responseTime, lowerResponseTime,
                        upperResponseTime);
            }
            double performabilityRewardSignal = interpolatedResponseTime + successRate;
            return new PerformabilityRewardSignal(performabilityRewardSignal);
        }

        @Override
        public String toString() {
            return Double.toString(getValue());
        }
    }

}
