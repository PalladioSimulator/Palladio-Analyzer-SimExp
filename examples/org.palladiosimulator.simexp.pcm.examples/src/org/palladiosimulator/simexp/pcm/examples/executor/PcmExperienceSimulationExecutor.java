package org.palladiosimulator.simexp.pcm.examples.executor;

import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.util.ExperimentProvider;
import org.palladiosimulator.simexp.service.registry.ServiceRegistry;

public abstract class PcmExperienceSimulationExecutor {
	
	private Experiment experiment;
	
	private static PcmExperienceSimulationExecutor instance;
	
	public PcmExperienceSimulationExecutor() {}
	
	
	public static PcmExperienceSimulationExecutor get() {
	    if (instance == null) {
	        instance = ServiceRegistry.get().findService(PcmExperienceSimulationExecutor.class).orElseThrow(() -> new RuntimeException("Failed to inject PcmExperienceSimulationExecutor"));
	        instance.init();
	    }
		return instance;
	}
	
	protected void init() {
        QVToReconfigurationManager.create(getReconfigurationRulesLocation());
	}

    protected void initializeExperiment(IPcmConfiguration config) {
        ExperimentLoader experimentLoader = new ExperimentLoader();
        String experimentFile = config.getExperimentFile();
        this.experiment = experimentLoader.loadExperiment(experimentFile);
        ExperimentProvider.create(this.experiment);
    }
    
    protected Experiment getExperiment() {
        return experiment;
    }
	
	public void execute() {
		createSimulator().run();
	}
	
	public abstract void evaluate();

	protected abstract String getExperimentFile();
	protected abstract ExperienceSimulator createSimulator();
	
	protected String getReconfigurationRulesLocation() {
		String path = experiment.getInitialModel().getReconfigurationRules().getFolderUri();
		experiment.getInitialModel().setReconfigurationRules(null);
		return path;
	}
	
}
