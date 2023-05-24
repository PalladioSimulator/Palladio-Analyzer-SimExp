package org.palladiosimulator.simexp.pcm.examples.executor;

import org.apache.log4j.Logger;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.util.ExperimentProvider;
import org.palladiosimulator.simexp.pcm.util.SimulationParameterConfiguration;

public abstract class PcmExperienceSimulationExecutor implements SimulationExecutor {
    
    protected static final Logger LOGGER = Logger.getLogger(PcmExperienceSimulationExecutor.class.getName());
	
	protected final Experiment experiment;
	protected final SimulationParameterConfiguration simulationParameters;
	
	
	public PcmExperienceSimulationExecutor(Experiment experiment, SimulationParameterConfiguration simulationParameters) {
		this.experiment = experiment;
		ExperimentProvider.create(this.experiment);
		QVToReconfigurationManager.create(getReconfigurationRulesLocation());
		this.simulationParameters = simulationParameters;
	}

	
	@Override
	public void execute() {
		createSimulator().run();
	}

	protected abstract ExperienceSimulator createSimulator();
	
	private String getReconfigurationRulesLocation() {
		String path = experiment.getInitialModel().getReconfigurationRules().getFolderUri();
		experiment.getInitialModel().setReconfigurationRules(null);
		return path;
	}
	
}
