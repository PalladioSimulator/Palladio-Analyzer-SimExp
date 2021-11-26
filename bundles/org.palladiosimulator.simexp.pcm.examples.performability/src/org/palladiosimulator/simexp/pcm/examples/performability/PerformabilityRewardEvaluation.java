package org.palladiosimulator.simexp.pcm.examples.performability;

import java.util.Locale;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.state.StateQuantity;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.RewardImpl;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;

public class PerformabilityRewardEvaluation implements RewardEvaluator {
    
    private static final Logger LOGGER = Logger.getLogger(PerformabilityRewardEvaluation.class.getName());
    
    private final PcmMeasurementSpecification responseTimeMeasurementSpec;
    private final PcmMeasurementSpecification systemResultExectutionTypeTimeMeasurementSpec;

    public PerformabilityRewardEvaluation(PcmMeasurementSpecification responseTimeMeasurementSpec, PcmMeasurementSpecification systemResultExectutionTypeTimeMeasurementSpec) {
        this.responseTimeMeasurementSpec = responseTimeMeasurementSpec;
        this.systemResultExectutionTypeTimeMeasurementSpec = systemResultExectutionTypeTimeMeasurementSpec;
    }
    
    @Override
    public Reward<Double> evaluate(StateQuantity quantity) {
        /**
         * method implements the reward signal based on the simulated measured response times;
         * reward signal will be used by {@link PerformabilityEvaluator.evaluate} to calculate 
         * the total reward based on the performability metric
         * 
         * */
        LOGGER.debug(String.format("Quantified state: %s", quantity.toString()));
        double measuredResponseTime = 0.0;
        
        // filter simulated response time measurement from set (key: Usage Scenario: UsageScenario_Response Time)
        Optional<SimulatedMeasurement> simulatedResponseTimeMeasurement = quantity.findMeasurementWith(responseTimeMeasurementSpec);
        // FIXME: add systemREsultExecutionTypeTimeMeasurementSpec
        if (!simulatedResponseTimeMeasurement.isPresent()) {
            LOGGER.error(String.format("No simulated response time measurements available for quantified state: %s; check your measurement specification."
                    , quantity.getMeasurements().toString()));
        } else {
            LOGGER.info(String.format("Simulated response time measurements available for quantified state %s", quantity.toString()));
            measuredResponseTime = simulatedResponseTimeMeasurement.get().getValue();
        }
        
        PerformabilityRewardSignal rewardSignal = PerformabilityRewardSignal.create(measuredResponseTime);
        LOGGER.debug(String.format(Locale.ENGLISH, "Performability reward signal for quantified state: %.10f", rewardSignal.getValue()));
        return rewardSignal;
    }
    
    private static class PerformabilityRewardSignal extends RewardImpl<Double> {

        private PerformabilityRewardSignal(double responseTime) {
            super.setValue(responseTime);
        }

        public static PerformabilityRewardSignal create(double responseTime) {
            return new PerformabilityRewardSignal(responseTime);
        }

        @Override
        public String toString() {
            return Double.toString(getValue());
        }
    }

}
