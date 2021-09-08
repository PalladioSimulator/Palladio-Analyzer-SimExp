package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import java.util.Set;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.state.StateQuantity;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
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
        
        double responseTime = 0.0;
        
        Set<SimulatedMeasurementSpecification> tmpMeasuremntSpecs = quantity.getMeasurementSpecs();
        Set<SimulatedMeasurement> tmpMeasurments = quantity.getMeasurements();
        
        for (SimulatedMeasurement simulatedMeasurement : tmpMeasurments) {
            LOGGER.info(String.format("SimulatedMeasurement: %s", simulatedMeasurement.toString()));
        }
        
        return null;
    }

}
