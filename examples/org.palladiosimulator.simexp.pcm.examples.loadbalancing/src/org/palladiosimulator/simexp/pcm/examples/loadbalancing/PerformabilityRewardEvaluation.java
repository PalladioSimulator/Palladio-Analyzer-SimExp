package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import java.util.List;

import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.reward.ThresholdBasedRewardEvaluator;
import org.palladiosimulator.simexp.core.state.StateQuantity;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;

public class PerformabilityRewardEvaluation extends ThresholdBasedRewardEvaluator {

    public PerformabilityRewardEvaluation(List<Pair<SimulatedMeasurementSpecification, Threshold>> thresholds) {
        super(thresholds);
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public Reward<Integer> evaluate(StateQuantity quantity) {
        /**
         * skeleton method to implement a performablity-metric (expectation of response times)
         * 
         * */
        
        return null;
    }

}
