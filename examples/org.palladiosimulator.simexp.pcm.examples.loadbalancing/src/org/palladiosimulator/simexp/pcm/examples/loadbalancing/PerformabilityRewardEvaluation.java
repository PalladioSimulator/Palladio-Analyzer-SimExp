package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.state.StateQuantity;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.RewardImpl;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;

public class PerformabilityRewardEvaluation implements RewardEvaluator {
    
    private static final Logger LOGGER = Logger.getLogger(PerformabilityRewardEvaluation.class.getName());
    
    private PcmMeasurementSpecification responseTimeMeasurementSpec;

    public PerformabilityRewardEvaluation(PcmMeasurementSpecification responseTimeMeasurementSpec) {
        this.responseTimeMeasurementSpec = responseTimeMeasurementSpec;
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
        List<SimulatedMeasurement> responseTimeMeasurement = quantity.getMeasurements()
                .stream()
                .filter(rt -> rt.getSpecification().getId().equals(responseTimeMeasurementSpec.getId())) 
                .collect(Collectors.toList());
        
        if (responseTimeMeasurement.size() != 1) {
            LOGGER.error(String.format("Check your measurement specification. Quantified state contains no measured response times: %s"
                    , quantity.getMeasurements().toString()));
        } else {
            measuredResponseTime = responseTimeMeasurement.get(0).getValue();
        }
        
        return PerformabilityRewardSignal.create(measuredResponseTime);
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
