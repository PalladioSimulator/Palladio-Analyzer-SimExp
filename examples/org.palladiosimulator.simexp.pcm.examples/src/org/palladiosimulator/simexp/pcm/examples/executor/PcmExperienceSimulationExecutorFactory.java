package org.palladiosimulator.simexp.pcm.examples.executor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.statespace.SelfAdaptiveSystemStateSpaceNavigator;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.pcm.builder.PcmExperienceSimulationBuilder;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simexp.pcm.util.SimulationParameters;

import tools.mdsd.probdist.api.apache.supplier.MultinomialDistributionSupplier;
import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;
import tools.mdsd.probdist.api.parser.ParameterParser;

public abstract class PcmExperienceSimulationExecutorFactory<T extends SimulatedMeasurementSpecification> {
	protected final Experiment experiment;
	protected final DynamicBayesianNetwork dbn;
	protected final List<T> specs;
	protected final SimulationParameters params;
	
	protected final IProbabilityDistributionFactory distributionFactory;
	protected final IProbabilityDistributionRegistry probabilityDistributionRegistry;
	protected final ParameterParser parameterParser;
	protected final IProbabilityDistributionRepositoryLookup probDistRepoLookup;
	protected final IExperimentProvider experimentProvider;
	
	public PcmExperienceSimulationExecutorFactory(Experiment experiment, DynamicBayesianNetwork dbn, List<T> specs, SimulationParameters params,
			IProbabilityDistributionFactory distributionFactory, IProbabilityDistributionRegistry probabilityDistributionRegistry, ParameterParser parameterParser, 
			IProbabilityDistributionRepositoryLookup probDistRepoLookup, IExperimentProvider experimentProvider) {
		this.experiment = experiment;
		this.dbn = dbn;
		this.specs = specs;
		this.params = params;
		this.distributionFactory = distributionFactory;
		this.probabilityDistributionRegistry = probabilityDistributionRegistry;
		this.parameterParser = parameterParser;
		this.probDistRepoLookup = probDistRepoLookup;
		this.experimentProvider = experimentProvider;
		
		probabilityDistributionRegistry.register(new MultinomialDistributionSupplier(parameterParser, probDistRepoLookup));
	}
	
	public abstract PcmExperienceSimulationExecutor create();
	
	protected ExperienceSimulator createExperienceSimulator(Experiment experiment, List<? extends SimulatedMeasurementSpecification> specs, 
    		List<ExperienceSimulationRunner> runners, SimulationParameters params, Initializable beforeExecution, EnvironmentProcess envProcess,
    		SelfAdaptiveSystemStateSpaceNavigator navigator, Policy<Action<?>> reconfStrategy, Set<Reconfiguration<?>> reconfigurations, 
    		RewardEvaluator evaluator, boolean hidden) {
    	
    	return PcmExperienceSimulationBuilder.newBuilder(experimentProvider)
    			.makeGlobalPcmSettings()
    				.withInitialExperiment(experiment)
    				.andSimulatedMeasurementSpecs(new HashSet<>(specs))
    				.addExperienceSimulationRunners(new HashSet<>(runners))
    				.done()
    			.createSimulationConfiguration()
    				.withSimulationID(params.getSimulationID())
    				.withNumberOfRuns(params.getNumberOfRuns())
    				.andNumberOfSimulationsPerRun(params.getNumberOfSimulationsPerRun())
    				.andOptionalExecutionBeforeEachRun(beforeExecution)
    				.done()
    			.specifySelfAdaptiveSystemState()
    				.asEnvironmentalDrivenProcess(envProcess)
    				.asPartiallyEnvironmentalDrivenProcess(navigator)
    				.asHiddenProcess(hidden)
    				.done()
    			.createReconfigurationSpace()
    				.addReconfigurations(reconfigurations)
    				.andReconfigurationStrategy(reconfStrategy)
    				.done()
    			.specifyRewardHandling()
    				.withRewardEvaluator(evaluator)
    				.done()
    			.build();	
    }
}
