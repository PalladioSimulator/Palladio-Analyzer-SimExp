package org.palladiosimulator.simexp.pcm.examples.hri;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.evaluation.ExpectedRewardEvaluator;
import org.palladiosimulator.simexp.core.evaluation.TotalRewardCalculation;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.util.SimulationParameterConfiguration;

public class RobotCognitionSimulationExecutor extends PcmExperienceSimulationExecutor {
    
    private static final Logger LOGGER = Logger.getLogger(RobotCognitionSimulationExecutor.class.getName());
	
	public static final double UPPER_THRESHOLD_RT = 0.1;
	public static final double LOWER_THRESHOLD_REL = 0.9;
	
	public final static URI UNCERTAINTY_MODEL_URI = URI.createPlatformResourceURI("/org.palladiosimulator.dependability.ml.hri/RobotCognitionUncertaintyModel.uncertainty", true);
	
	private final ReconfigurationStrategy<? extends Reconfiguration<?>> reconfigurationStrategy;
	
	public RobotCognitionSimulationExecutor(ExperienceSimulator experienceSimulator, Experiment experiment , SimulationParameterConfiguration simulationParameters, 
			ReconfigurationStrategy<? extends Reconfiguration<?>> reconfigurationStrategy) {
	    super(experienceSimulator, experiment, simulationParameters);
		this.reconfigurationStrategy = reconfigurationStrategy;
	}
	
	public static final class RobotCognitionSimulationExecutorFactory {
	    public RobotCognitionSimulationExecutor create(ExperienceSimulator experienceSimulator, Experiment experiment, SimulationParameterConfiguration simulationParameters,
	    		ReconfigurationStrategy<? extends Reconfiguration<?>> reconfigurationStrategy) {
	        return new RobotCognitionSimulationExecutor(experienceSimulator, experiment, simulationParameters, reconfigurationStrategy);
	    }
	}
	
	@Override
	public void evaluate() {
		String sampleSpaceId = SimulatedExperienceConstants.constructSampleSpaceId(simulationParameters.getSimulationID(), reconfigurationStrategy.getId());
		TotalRewardCalculation evaluator = new ExpectedRewardEvaluator(simulationParameters.getSimulationID(), sampleSpaceId);
		LOGGER.info("***********************************************************************");
		LOGGER.info(String.format("The total Reward of policy %1s is %2s", reconfigurationStrategy.getId(), evaluator.computeTotalReward()));
		LOGGER.info("***********************************************************************");
	}
}
