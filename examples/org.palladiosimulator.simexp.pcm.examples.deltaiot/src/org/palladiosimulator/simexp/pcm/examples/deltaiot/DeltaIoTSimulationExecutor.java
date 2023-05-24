package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import org.apache.log4j.Logger;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.evaluation.SimulatedExperienceEvaluator;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.util.SimulationParameterConfiguration;

public class DeltaIoTSimulationExecutor extends PcmExperienceSimulationExecutor {
    
    private static final Logger LOGGER = Logger.getLogger(DeltaIoTSimulationExecutor.class.getName());

	public final static String DELTAIOT_PATH = "/org.palladiosimulator.envdyn.examples.deltaiot";
	public final static String DISTRIBUTION_FACTORS = DELTAIOT_PATH + "/model/DeltaIoTReconfigurationParams.reconfigurationparams";
	public final static String PRISM_FOLDER = "prism";

	private final Policy<Action<?>> reconfSelectionPolicy;

	public DeltaIoTSimulationExecutor(ExperienceSimulator experienceSimulator, Experiment experiment,  SimulationParameterConfiguration simulationParameters, 
			Policy<Action<?>> reconfSelectionPolicy) {
		super(experienceSimulator, experiment, simulationParameters);
		this.reconfSelectionPolicy = reconfSelectionPolicy;
		
	}

	public static final class DeltaIoTSimulationExecutorFactory {
	    public DeltaIoTSimulationExecutor create(ExperienceSimulator experienceSimulator, Experiment experiment, SimulationParameterConfiguration simulationParameters, 
	    		Policy<Action<?>> reconfSelectionPolicy) {
	        return new DeltaIoTSimulationExecutor(experienceSimulator, experiment, simulationParameters, reconfSelectionPolicy);
	    }
	}
	
	@Override
	public void evaluate() {
		String sampleSpaceId = SimulatedExperienceConstants.constructSampleSpaceId(simulationParameters.getSimulationID(),
				reconfSelectionPolicy.getId());
		Double totalReward = SimulatedExperienceEvaluator.of(simulationParameters.getSimulationID(), sampleSpaceId).computeTotalReward();
		LOGGER.info("***********************************************************************");
		LOGGER.info(String.format("The total Reward of policy %1s is %2s", reconfSelectionPolicy.getId(), totalReward));
		LOGGER.info("***********************************************************************");
	}
}
