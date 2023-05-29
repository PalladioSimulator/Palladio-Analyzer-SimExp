package org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing;

import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.evaluation.PerformabilityEvaluator;
import org.palladiosimulator.simexp.core.evaluation.TotalRewardCalculation;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.util.SimulationParameters;

public class FaultTolerantLoadBalancingSimulationExecutor extends PcmExperienceSimulationExecutor {
    
    public static final Threshold LOWER_THRESHOLD_RT = Threshold.greaterThanOrEqualTo(0.1);
    public static final Threshold UPPER_THRESHOLD_RT = Threshold.lessThanOrEqualTo(0.4);
	
    public static final String SERVER_FAILURE_TEMPLATE_ID = "_VtIJEPtrEeuPUtFH1XJrHw";
    public static final String LOAD_BALANCER_ID = "_NvLi8AEmEeS7FKokKTKFow";
		
	private FaultTolerantLoadBalancingSimulationExecutor(ExperienceSimulator experienceSimulator, Experiment experiment, SimulationParameters simulationParameters, 
			Policy<Action<?>> reconfSelectionPolicy, TotalRewardCalculation rewardCalculation) {
		super(experienceSimulator, experiment, simulationParameters, reconfSelectionPolicy, rewardCalculation);
	}
	
	public static final class FaultTolerantLoadBalancingSimulationExecutorFactory {
	    public FaultTolerantLoadBalancingSimulationExecutor create(ExperienceSimulator experienceSimulator, Experiment experiment, SimulationParameters simulationParameters, 
				Policy<Action<?>> reconfSelectionPolicy, TotalRewardCalculation rewardCalculation) {
	        return new FaultTolerantLoadBalancingSimulationExecutor(experienceSimulator, experiment, simulationParameters, reconfSelectionPolicy, rewardCalculation);
	    }
	}

	@Override
	public void evaluate() {
		String sampleSpaceId = SimulatedExperienceConstants.constructSampleSpaceId(simulationParameters.getSimulationID(), reconfSelectionPolicy.getId());
		TotalRewardCalculation evaluator = new PerformabilityEvaluator(simulationParameters.getSimulationID(), sampleSpaceId);
		LOGGER.info("***********************************************************************");
		double computeTotalReward = evaluator.computeTotalReward();
        LOGGER.info(String.format("The total Reward of policy %1s is %2s", reconfSelectionPolicy.getId(), computeTotalReward));
		LOGGER.info("***********************************************************************");
	}
}
