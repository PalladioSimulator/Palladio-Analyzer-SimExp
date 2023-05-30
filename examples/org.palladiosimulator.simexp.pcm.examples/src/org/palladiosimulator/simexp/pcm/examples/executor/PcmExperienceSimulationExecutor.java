package org.palladiosimulator.simexp.pcm.examples.executor;

import org.apache.log4j.Logger;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simexp.pcm.util.SimulationParameterConfiguration;

public abstract class PcmExperienceSimulationExecutor implements SimulationExecutor {
    
    protected static final Logger LOGGER = Logger.getLogger(PcmExperienceSimulationExecutor.class.getName());
	
	protected final Experiment experiment;
	protected final SimulationParameterConfiguration simulationParameters;
	protected final IExperimentProvider experimentProvider;
	protected final IQVToReconfigurationManager qvtoReconfigurationManager;
	
	
	public PcmExperienceSimulationExecutor(Experiment experiment, SimulationParameterConfiguration simulationParameters, IExperimentProvider experimentProvider) {
		this.experiment = experiment;
		this.experimentProvider = experimentProvider;
		this.qvtoReconfigurationManager = new QVToReconfigurationManager(getReconfigurationRulesLocation());
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
