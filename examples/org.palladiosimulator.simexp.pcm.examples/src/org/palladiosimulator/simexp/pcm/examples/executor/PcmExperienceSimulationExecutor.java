package org.palladiosimulator.simexp.pcm.examples.executor;

import org.apache.log4j.Logger;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.evaluation.TotalRewardCalculation;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.util.ExperimentProvider;
import org.palladiosimulator.simexp.pcm.util.SimulationParameters;
import org.palladiosimulator.simexp.service.registry.ServiceRegistry;

public class PcmExperienceSimulationExecutor implements SimulationExecutor {
    
    protected static final Logger LOGGER = Logger.getLogger(PcmExperienceSimulationExecutor.class.getName());
	
    protected final ExperienceSimulator experienceSimulator;
	protected final Experiment experiment;
	protected final SimulationParameters simulationParameters;
	protected final Policy<Action<?>> reconfSelectionPolicy;
	protected final TotalRewardCalculation rewardCalculation;
	
//	private static PcmExperienceSimulationExecutor instance = Guice.createInjector(new ExecutorBindingModule()).getInstance(PcmExperienceSimulationExecutor.class);
	private static PcmExperienceSimulationExecutor instance;
	
	public PcmExperienceSimulationExecutor(ExperienceSimulator experienceSimulator, Experiment experiment, SimulationParameters simulationParameters,
			Policy<Action<?>> reconfSelectionPolicy, TotalRewardCalculation rewardCalculation) {
		this.experienceSimulator = experienceSimulator;
		this.experiment = experiment;
		this.simulationParameters = simulationParameters;
		this.reconfSelectionPolicy = reconfSelectionPolicy;
		this.rewardCalculation = rewardCalculation;
		
		ExperimentProvider.create(this.experiment);
		QVToReconfigurationManager.create(getReconfigurationRulesLocation());
	}

	public static PcmExperienceSimulationExecutor get() {
	    if (instance == null) {
	        instance = ServiceRegistry.get()
	        		.findService(PcmExperienceSimulationExecutor.class)
	        		.orElseThrow(() -> new RuntimeException("Failed to inject PcmExperienceSimulationExecutor"));
	    }
		return instance;
	}
	
	@Override
	public void execute() {
		experienceSimulator.run();
	}
	
	private String getReconfigurationRulesLocation() {
		String path = experiment.getInitialModel().getReconfigurationRules().getFolderUri();
		experiment.getInitialModel().setReconfigurationRules(null);
		return path;
	}

	@Override
	public void evaluate() {
		double totalReward = rewardCalculation.computeTotalReward();
		LOGGER.info("***********************************************************************");
		LOGGER.info(String.format("The total Reward of policy %1s is %2s", reconfSelectionPolicy.getId(), totalReward));
		LOGGER.info("***********************************************************************");
	}
}
