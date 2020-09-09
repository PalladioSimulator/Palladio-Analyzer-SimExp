package org.palladiosimulator.simexp.pcm.examples.executor;

import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.examples.binding.ExecutorBindingModule;
import org.palladiosimulator.simexp.pcm.util.ExperimentProvider;

import com.google.inject.Guice;

public abstract class PcmExperienceSimulationExecutor {
	
	protected final Experiment experiment;
	
	private static PcmExperienceSimulationExecutor instance = Guice.createInjector(new ExecutorBindingModule()).getInstance(PcmExperienceSimulationExecutor.class);
	
	public PcmExperienceSimulationExecutor() {
		this.experiment = new ExperimentLoader().loadExperiment(getExperimentFile());
		ExperimentProvider.create(this.experiment);
		QVToReconfigurationManager.create(getReconfigurationRulesLocation());
	}

	public static PcmExperienceSimulationExecutor get() {
		return instance;
	}
	
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
