package org.palladiosimulator.simexp.pcm.examples;

import java.util.List;

import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;

public interface ISimExpSimulationConfiguration {
    
    String getSimulationId();
    
    Policy<Action<?>> getReconfigurationPolicy();

    RewardEvaluator getRewardEvaluator();

    List<PcmMeasurementSpecification> getPcmSpecs();

}
