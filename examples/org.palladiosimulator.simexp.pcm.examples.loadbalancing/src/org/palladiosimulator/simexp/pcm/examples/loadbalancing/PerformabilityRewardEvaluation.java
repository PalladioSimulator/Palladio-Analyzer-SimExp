package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
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
        
        LOGGER.info(String.format("Quantified state: %s", quantity.toString()));
        
        /**
         * skeleton method to implement the reward signal based on response times;
         * will be later used to calculate the total reward
         * 
         * */
        
        double measuredResponseTime = 0.0;
        
        String key = responseTimeMeasurementSpec.getName();
        
        List<SimulatedMeasurement> responseTimeMeasurement = quantity.getMeasurements()
                .stream()
                // lookup response time measurement: Usage Scenario: UsageScenario_Response Time
                .filter(rt -> rt.getSpecification().equals(responseTimeMeasurementSpec.getName())) 
                .collect(Collectors.toList());
        
        if (responseTimeMeasurement.size() != 1) {
            LOGGER.error(String.format("No measured response time attached to quantified state: %s", quantity.getMeasurements().toString()));
        } else {
            measuredResponseTime = responseTimeMeasurement.get(0).getValue();
        }
        
        
        for (SimulatedMeasurement simulatedMeasurement : quantity.getMeasurements()) {
            LOGGER.info(String.format("SimulatedMeasurement: %s", simulatedMeasurement.toString()));
        }
        
        return PerformabilityRewardSignal.create(measuredResponseTime);
    }
    
    private static class PerformabilityRewardSignal extends RewardImpl<Double> {

        private PerformabilityRewardSignal() {
            super.setValue(0.0);
        }

        public static PerformabilityRewardSignal create(double responseTime) {
            return new PerformabilityRewardSignal();
        }

        @Override
        public String toString() {
            return Double.toString(getValue());
        }
    }

}
