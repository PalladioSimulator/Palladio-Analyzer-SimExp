package org.palladiosimulator.simexp.pcm.examples.loadbalancing;


import org.apache.log4j.Logger;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.evaluation.SimulatedExperienceEvaluator;
import org.palladiosimulator.simexp.core.evaluation.TotalRewardCalculation;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.util.SimulationParameterConfiguration;

public class LoadBalancingSimulationExecutor extends PcmExperienceSimulationExecutor {
    
    private static final Logger LOGGER = Logger.getLogger(LoadBalancingSimulationExecutor.class.getName());

    public final static double UPPER_THRESHOLD_RT = 2.0;
	public final static double LOWER_THRESHOLD_RT = 0.3;
	
	private final Policy<Action<?>> reconfSelectionPolicy;
	
	private LoadBalancingSimulationExecutor(ExperienceSimulator experienceSimulator, Experiment experiment, SimulationParameterConfiguration simulationParameters,
			Policy<Action<?>> reconfSelectionPolicy) {
		super(experienceSimulator, experiment, simulationParameters);
		this.reconfSelectionPolicy = reconfSelectionPolicy;
	}
	
	public static final class LoadBalancingSimulationExecutorFactory {
	    public LoadBalancingSimulationExecutor create(ExperienceSimulator experienceSimulator, Experiment experiment, SimulationParameterConfiguration simulationParameters, 
	    		Policy<Action<?>> reconfSelectionPolicy) {
	        return new LoadBalancingSimulationExecutor(experienceSimulator, experiment, simulationParameters, reconfSelectionPolicy);
	    }
	}

	@Override
	public void evaluate() {
		String sampleSpaceId = SimulatedExperienceConstants.constructSampleSpaceId(simulationParameters.getSimulationID(), reconfSelectionPolicy.getId());
		TotalRewardCalculation evaluator = SimulatedExperienceEvaluator.of(simulationParameters.getSimulationID(), sampleSpaceId);
		LOGGER.info("***********************************************************************");
		LOGGER.info(String.format("The total Reward of policy %1s is %2s", reconfSelectionPolicy.getId(), evaluator.computeTotalReward()));
		LOGGER.info("***********************************************************************");
	}
}
