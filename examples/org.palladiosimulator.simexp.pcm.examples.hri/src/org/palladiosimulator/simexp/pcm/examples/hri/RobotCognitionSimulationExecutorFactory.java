package org.palladiosimulator.simexp.pcm.examples.hri;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.analyzer.workflow.ConstantsContainer;
import org.palladiosimulator.dependability.reliability.uncertainty.UncertaintyRepository;
import org.palladiosimulator.dependability.reliability.uncertainty.solver.api.UncertaintyBasedReliabilityPredictionConfig;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.evaluation.ExpectedRewardEvaluator;
import org.palladiosimulator.simexp.core.evaluation.TotalRewardCalculation;
import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.process.PcmExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.reliability.entity.PcmRelSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.reliability.process.PcmRelExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simexp.pcm.util.SimulationParameters;
import org.palladiosimulator.solver.runconfig.PCMSolverWorkflowRunConfiguration;

import de.uka.ipd.sdq.workflow.mdsd.blackboard.ResourceSetPartition;
import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;
import tools.mdsd.probdist.api.parser.ParameterParser;

public class RobotCognitionSimulationExecutorFactory extends PcmExperienceSimulationExecutorFactory<PcmMeasurementSpecification> {
	public static final double UPPER_THRESHOLD_RT = 0.1;
	public static final double LOWER_THRESHOLD_REL = 0.9;
	
	public final static URI UNCERTAINTY_MODEL_URI = URI.createPlatformResourceURI("/org.palladiosimulator.dependability.ml.hri/RobotCognitionUncertaintyModel.uncertainty", true);

	public RobotCognitionSimulationExecutorFactory(Experiment experiment, DynamicBayesianNetwork dbn,
			List<PcmMeasurementSpecification> specs, SimulationParameters params,
			IProbabilityDistributionFactory distributionFactory,
			IProbabilityDistributionRegistry probabilityDistributionRegistry, ParameterParser parameterParser,
			IProbabilityDistributionRepositoryLookup probDistRepoLookup, IExperimentProvider experimentProvider) {
		super(experiment, dbn, specs, params, distributionFactory, probabilityDistributionRegistry, parameterParser,
				probDistRepoLookup, experimentProvider);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public PcmExperienceSimulationExecutor create() {
		UsageScenario usageScenario = experiment.getInitialModel().getUsageModel().getUsageScenario_UsageModel().get(0);
		SimulatedMeasurementSpecification reliabilitySpec = new PcmRelSimulatedMeasurementSpec(usageScenario);
		List<SimulatedMeasurementSpecification> relSpecs = new ArrayList<>(specs);
		relSpecs.add(reliabilitySpec);
			
		UncertaintyBasedReliabilityPredictionConfig predictionConfig = new UncertaintyBasedReliabilityPredictionConfig(createDefaultRunConfig(), null, loadUncertaintyRepository(), null);
		List<ExperienceSimulationRunner> runners = List.of(new PcmRelExperienceSimulationRunner(predictionConfig, 
				probabilityDistributionRegistry, distributionFactory, parameterParser, probDistRepoLookup), new PcmExperienceSimulationRunner(experimentProvider));
			
		ReconfigurationStrategy<? extends Reconfiguration<?>> reconfSelectionPolicy = new StaticSystemSimulation();
		Initializable beforeExecutionInitializable = new RobotCognitionBeforeExecutionInitialization(reconfSelectionPolicy, experimentProvider);
		EnvironmentProcess envProcess = RobotCognitionEnvironmentalDynamics.get(dbn);
		RewardEvaluator evaluator = new RealValuedRewardEvaluator(reliabilitySpec);
			
		Set<Reconfiguration<?>> reconfigurations = new HashSet<Reconfiguration<?>>(QVToReconfigurationManager.get().loadReconfigurations());
			
		ExperienceSimulator simulator = createExperienceSimulator(experiment, specs, runners, params, 
				beforeExecutionInitializable, envProcess, null, (Policy<Action<?>>) reconfSelectionPolicy, reconfigurations, evaluator, true);
			
		String sampleSpaceId = SimulatedExperienceConstants.constructSampleSpaceId(params.getSimulationID(), reconfSelectionPolicy.getId());
		TotalRewardCalculation rewardCalculation = new ExpectedRewardEvaluator(params.getSimulationID(), sampleSpaceId);
			
		return new PcmExperienceSimulationExecutor(simulator, experiment, params, (Policy<Action<?>>) reconfSelectionPolicy, rewardCalculation, experimentProvider);
	}
	
	private PCMSolverWorkflowRunConfiguration createDefaultRunConfig() {
		var config = new PCMSolverWorkflowRunConfiguration();
        config.setReliabilityAnalysis(true);
        config.setPrintMarkovStatistics(false);
        config.setPrintMarkovSingleResults(false);
        config.setSensitivityModelEnabled(false);
        config.setSensitivityModelFileName(null);
        config.setSensitivityLogFileName(null);
        
        config.setDeleteTemporaryDataAfterAnalysis(true);
        config.setDistance(1.0);
        config.setDomainSize(32);
        config.setIterationOverPhysicalSystemStatesEnabled(true);
        config.setMarkovModelReductionEnabled(true);
        config.setNumberOfEvaluatedSystemStates(1);
        config.setNumberOfEvaluatedSystemStatesEnabled(false);
        config.setSolvingTimeLimitEnabled(false);
        
        // TODO check
        config.setLogFile(null);
        config.setNumberOfEvaluatedSystemStatesEnabled(false);
        config.setNumberOfEvaluatedSystemStates(0);
        config.setNumberOfExactDecimalPlacesEnabled(false);
        config.setNumberOfExactDecimalPlaces(0);
        config.setSolvingTimeLimitEnabled(false);
        config.setMarkovModelStorageEnabled(false);
        config.setIterationOverPhysicalSystemStatesEnabled(true);
        // TODO check
        config.setMarkovEvaluationMode("POINTSOFFAILURE"); 
        config.setSaveResultsToFileEnabled(false);
        
        config.setRMIMiddlewareFile(ConstantsContainer.DEFAULT_RMI_MIDDLEWARE_REPOSITORY_FILE);
        config.setEventMiddlewareFile(ConstantsContainer.DEFAULT_EVENT_MIDDLEWARE_FILE);
        return config;
	}
    
    private UncertaintyRepository loadUncertaintyRepository() {
		var partition = new ResourceSetPartition();
		partition.loadModel(UNCERTAINTY_MODEL_URI);
		partition.resolveAllProxies();
		return (UncertaintyRepository) partition.getFirstContentElement(UNCERTAINTY_MODEL_URI);
	}
}
