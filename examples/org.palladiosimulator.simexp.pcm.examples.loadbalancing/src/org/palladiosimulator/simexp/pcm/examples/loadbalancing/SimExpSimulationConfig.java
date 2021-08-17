package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import java.util.List;

import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.pcm.examples.ISimExpSimulationConfiguration;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;

public class SimExpSimulationConfig implements ISimExpSimulationConfiguration {
    
    private final static String SIMULATION_ID = "LoadBalancing";
    
    
    private final static double THRESHOLD_UTIL_1 = 0.7;
    private final static double THRESHOLD_UTIL_2 = 0.5;
    public final static double UPPER_THRESHOLD_RT = 2.0;
    public final static double LOWER_THRESHOLD_RT = 1.0;
    
    private final Policy<Action<?>> reconfSelectionPolicy;
    private final List<PcmMeasurementSpecification> pcmSpecs;
    

    public SimExpSimulationConfig(List<PcmMeasurementSpecification> pcmSpecs) {
        this.pcmSpecs = pcmSpecs;
        this.reconfSelectionPolicy = new NStepLoadBalancerStrategy(1, pcmSpecs.get(0));
    }

    @Override
    public Policy<Action<?>> getReconfigurationPolicy() {
        return reconfSelectionPolicy;
    }
    
    


    @Override
    public List<PcmMeasurementSpecification> getPcmSpecs() {
        return this.pcmSpecs;
    }

    @Override
    public RewardEvaluator getRewardEvaluator() {
        return new LoadBalancingRewardEvaluation(upperResponseTimeThreshold(), cpuServer1Threshold(), cpuServer2Threshold());
    }
    
    private Pair<SimulatedMeasurementSpecification, Threshold> upperResponseTimeThreshold() {
        return Pair.of(pcmSpecs.get(0), Threshold.lessThanOrEqualTo(UPPER_THRESHOLD_RT));
    }
    
    private Pair<SimulatedMeasurementSpecification, Threshold> cpuServer1Threshold() {
        return Pair.of(pcmSpecs.get(1), Threshold.lessThanOrEqualTo(THRESHOLD_UTIL_1));
    }

    private Pair<SimulatedMeasurementSpecification, Threshold> cpuServer2Threshold() {
        return Pair.of(pcmSpecs.get(2), Threshold.lessThanOrEqualTo(THRESHOLD_UTIL_2));
    }

    @Override
    public String getSimulationId() {
        return SIMULATION_ID;
    }

}
