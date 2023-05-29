package org.palladiosimulator.simexp.pcm.examples.hri;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.evaluation.ExpectedRewardEvaluator;
import org.palladiosimulator.simexp.core.evaluation.TotalRewardCalculation;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.util.SimulationParameters;

public class RobotCognitionSimulationExecutor extends PcmExperienceSimulationExecutor {
    
    private static final Logger LOGGER = Logger.getLogger(RobotCognitionSimulationExecutor.class.getName());
	
	public static final double UPPER_THRESHOLD_RT = 0.1;
	public static final double LOWER_THRESHOLD_REL = 0.9;
	
	public final static URI UNCERTAINTY_MODEL_URI = URI.createPlatformResourceURI("/org.palladiosimulator.dependability.ml.hri/RobotCognitionUncertaintyModel.uncertainty", true);
	
	public RobotCognitionSimulationExecutor(ExperienceSimulator experienceSimulator, Experiment experiment , SimulationParameters simulationParameters, 
			Policy<Action<?>> reconfSelectionPolicy, TotalRewardCalculation rewardCalculation) {
	    super(experienceSimulator, experiment, simulationParameters, reconfSelectionPolicy, rewardCalculation);
	}
	
	public static final class RobotCognitionSimulationExecutorFactory {
	    public RobotCognitionSimulationExecutor create(ExperienceSimulator experienceSimulator, Experiment experiment, SimulationParameters simulationParameters,
	    		Policy<Action<?>> reconfSelectionPolicy, TotalRewardCalculation rewardCalculation) {
	        return new RobotCognitionSimulationExecutor(experienceSimulator, experiment, simulationParameters, reconfSelectionPolicy, rewardCalculation);
	    }
	}
	
	@Override
	public void evaluate() {
		String sampleSpaceId = SimulatedExperienceConstants.constructSampleSpaceId(simulationParameters.getSimulationID(), reconfSelectionPolicy.getId());
		TotalRewardCalculation evaluator = new ExpectedRewardEvaluator(simulationParameters.getSimulationID(), sampleSpaceId);
		LOGGER.info("***********************************************************************");
		LOGGER.info(String.format("The total Reward of policy %1s is %2s", reconfSelectionPolicy.getId(), evaluator.computeTotalReward()));
		LOGGER.info("***********************************************************************");
	}
}
