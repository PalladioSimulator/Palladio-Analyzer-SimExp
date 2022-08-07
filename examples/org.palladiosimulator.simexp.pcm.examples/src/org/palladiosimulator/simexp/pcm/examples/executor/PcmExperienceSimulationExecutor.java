package org.palladiosimulator.simexp.pcm.examples.executor;

import org.apache.log4j.Logger;
import org.palladiosimulator.core.simulation.SimulationExecution;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.util.ExperimentProvider;

public abstract class PcmExperienceSimulationExecutor implements SimulationExecution {
    
    protected static final Logger LOGGER = Logger.getLogger(PcmExperienceSimulationExecutor.class.getName());
	
    //private Path kmodelFile;
	protected Experiment experiment;
	
//	private static PcmExperienceSimulationExecutor instance = Guice.createInjector(new ExecutorBindingModule()).getInstance(PcmExperienceSimulationExecutor.class);
	//private static PcmExperienceSimulationExecutor instance;
	
	public PcmExperienceSimulationExecutor() {
	    String experimentFile = getExperimentFile();
		this.experiment = new ExperimentLoader().loadExperiment(experimentFile);
		ExperimentProvider.create(this.experiment);
		QVToReconfigurationManager.create(getReconfigurationRulesLocation());
	}

	/*public static PcmExperienceSimulationExecutor get() {
	    if (instance == null) {
	        instance = ServiceRegistry.get().findService(PcmExperienceSimulationExecutor.class).orElseThrow(() -> new RuntimeException("Failed to inject PcmExperienceSimulationExecutor"));
	    }
		return instance;
	}*/
	
	public void execute() {
		createSimulator().run();
	}
	
	public abstract void evaluate();

	protected abstract String getExperimentFile();
	protected abstract ExperienceSimulator createSimulator();
	
	private String getReconfigurationRulesLocation() {
		String path = experiment.getInitialModel().getReconfigurationRules().getFolderUri();
		experiment.getInitialModel().setReconfigurationRules(null);
		return path;
	}

}
