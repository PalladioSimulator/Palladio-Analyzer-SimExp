package org.palladiosimulator.simexp.pcm.examples.hri;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.analyzer.workflow.ConstantsContainer;
import org.palladiosimulator.dependability.reliability.uncertainty.UncertaintyRepository;
import org.palladiosimulator.dependability.reliability.uncertainty.solver.api.UncertaintyBasedReliabilityPredictionConfig;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.evaluation.ExpectedRewardEvaluator;
import org.palladiosimulator.simexp.core.evaluation.TotalRewardCalculation;
import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.state.StateQuantity;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.RewardImpl;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.builder.PcmExperienceSimulationBuilder;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.init.GlobalPcmBeforeExecutionInitialization;
import org.palladiosimulator.simexp.pcm.process.PcmExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.reliability.entity.PcmRelSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.reliability.process.PcmRelExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simexp.pcm.util.SimulationParameterConfiguration;
import org.palladiosimulator.solver.runconfig.PCMSolverWorkflowRunConfiguration;

import com.google.common.collect.Sets;

import de.uka.ipd.sdq.workflow.mdsd.blackboard.ResourceSetPartition;
import tools.mdsd.probdist.api.apache.supplier.MultinomialDistributionSupplier;
import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;
import tools.mdsd.probdist.api.parser.ParameterParser;

public class RobotCognitionSimulationExecutor extends PcmExperienceSimulationExecutor {
    
    private static final Logger LOGGER = Logger.getLogger(RobotCognitionSimulationExecutor.class.getName());

	public class RobotCognitionBeforeExecutionInitialization extends GlobalPcmBeforeExecutionInitialization {
		
		public RobotCognitionBeforeExecutionInitialization(IExperimentProvider experimentProvider, IQVToReconfigurationManager qvtoReconfigurationManager) {
			super(experimentProvider, qvtoReconfigurationManager);
		}

		@Override
		public void initialize() {
			super.initialize();
			
			if (reconfigurationStrategy instanceof Initializable) {
				Initializable.class.cast(reconfigurationStrategy).initialize();
			}
		}
		
	}
	
	public static final double UPPER_THRESHOLD_RT = 0.1;
	public static final double LOWER_THRESHOLD_REL = 0.9;
	
	private final static URI UNCERTAINTY_MODEL_URI = URI.createPlatformResourceURI("/org.palladiosimulator.dependability.ml.hri/RobotCognitionUncertaintyModel.uncertainty", true);
	
	private final DynamicBayesianNetwork dbn;
	private final PcmMeasurementSpecification responseTimeSpec;
	private final SimulatedMeasurementSpecification reliabilitySpec;
	private final ReconfigurationStrategy<?> reconfigurationStrategy;
	private final IProbabilityDistributionRegistry probabilityDistributionRegistry;
	private final IProbabilityDistributionFactory probabilityDistributionFactory;
	private final ParameterParser parameterParser;
	private final IProbabilityDistributionRepositoryLookup probDistRepoLookup;
	
	public RobotCognitionSimulationExecutor(Experiment experiment, DynamicBayesianNetwork dbn, 
			IProbabilityDistributionRegistry probabilityDistributionRegistry, 
			IProbabilityDistributionFactory probabilityDistributionFactory, ParameterParser parameterParser, 
			IProbabilityDistributionRepositoryLookup probDistRepoLookup, SimulationParameterConfiguration simulationParameters,
			List<PcmMeasurementSpecification> pcmSpecs, IExperimentProvider experimentProvider, IQVToReconfigurationManager qvtoReconfigurationManager) {
	    super(experiment, simulationParameters, experimentProvider, qvtoReconfigurationManager);
		this.dbn = dbn;
		this.responseTimeSpec = pcmSpecs.get(0);
		this.reliabilitySpec = buildReliabilitySpec();
		//this.reconfigurationStrategy = new ReliabilityPrioritizedStrategy(responseTimeSpec);
		//this.reconfigurationStrategy = new RandomizedAdaptationStrategy(responseTimeSpec);
		this.reconfigurationStrategy = new StaticSystemSimulation();
		
		this.probabilityDistributionRegistry = probabilityDistributionRegistry;
		probabilityDistributionRegistry.register(new MultinomialDistributionSupplier(parameterParser, probDistRepoLookup));
		this.probabilityDistributionFactory = probabilityDistributionFactory;
		this.parameterParser = parameterParser;
		this.probDistRepoLookup = probDistRepoLookup;
	}
	
	public static final class RobotCognitionSimulationExecutorFactory {
	    public RobotCognitionSimulationExecutor create(Experiment experiment, DynamicBayesianNetwork dbn, 
	    		IProbabilityDistributionRegistry probabilityDistributionRegistry, 
	    		IProbabilityDistributionFactory probabilityDistributionFactory, ParameterParser parameterParser, 
	    		IProbabilityDistributionRepositoryLookup probDistRepoLookup, SimulationParameterConfiguration simulationParameters,
	    		List<PcmMeasurementSpecification> pcmSpecs, IExperimentProvider experimentProvider, IQVToReconfigurationManager qvtoReconfigurationManager) {
	        return new RobotCognitionSimulationExecutor(experiment, dbn, probabilityDistributionRegistry, probabilityDistributionFactory, 
	        		parameterParser, probDistRepoLookup, simulationParameters, pcmSpecs, experimentProvider, qvtoReconfigurationManager);
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

	@Override
	protected ExperienceSimulator createSimulator() {
		return PcmExperienceSimulationBuilder.newBuilder(experimentProvider, qvtoReconfigurationManager)
				.makeGlobalPcmSettings()
					.withInitialExperiment(experiment)
					.andSimulatedMeasurementSpecs(Sets.newHashSet(responseTimeSpec, reliabilitySpec))
					.addExperienceSimulationRunner(createPcmRelExperienceSimulationRunner(probabilityDistributionRegistry, probabilityDistributionFactory, parameterParser, probDistRepoLookup))
					.addExperienceSimulationRunner(new PcmExperienceSimulationRunner(experimentProvider))
					.done()
				.createSimulationConfiguration()
					.withSimulationID(simulationParameters.getSimulationID()) // RobotCognition
					.withNumberOfRuns(simulationParameters.getNumberOfRuns()) //50
					.andNumberOfSimulationsPerRun(simulationParameters.getNumberOfSimulationsPerRun()) //100
					.andOptionalExecutionBeforeEachRun(new RobotCognitionBeforeExecutionInitialization(experimentProvider, qvtoReconfigurationManager))
					.done()
				.specifySelfAdaptiveSystemState()
				  	.asEnvironmentalDrivenProcess(RobotCognitionEnvironmentalDynamics.get(dbn))
				  	.isHiddenProcess()
					.done()
				.createReconfigurationSpace()
					.addReconfigurations(getAllReconfigurations())
				  	.andReconfigurationStrategy(reconfigurationStrategy)
				  	.done()
				.specifyRewardHandling()
				  	.withRewardEvaluator(getPureReliabilityBasedRewardEvaluator())
				  	.done()
				.build();
	}
	
	private Set<Reconfiguration<?>> getAllReconfigurations() {
		return Sets.newHashSet(qvtoReconfigurationManager.loadReconfigurations());
	}
	
	private RewardEvaluator getRewardEvaluator() {
		return new RobotCognitionRewardEvaluation(responseTimeThreshold(), reliabilityThreshold());
	}
	
	private RewardEvaluator getPureReliabilityBasedRewardEvaluator() {
		class RealValuedReward extends RewardImpl<Double> {
			
			private RealValuedReward(double value) {
				super.setValue(value);
			}
			
			@Override
			public String toString() {
				return Double.toString(getValue());
			}

		}
		
		return new RewardEvaluator() {
			
			@Override
			public Reward<?> evaluate(StateQuantity quantifiedState) {
				var reliability = quantifiedState.findMeasurementWith(reliabilitySpec).get().getValue();
				return new RealValuedReward(reliability);
			}
		};
	}

	private Pair<SimulatedMeasurementSpecification, Threshold> responseTimeThreshold() {
		return Pair.of(responseTimeSpec, Threshold.lessThanOrEqualTo(UPPER_THRESHOLD_RT));
	}
	
	private Pair<SimulatedMeasurementSpecification, Threshold> reliabilityThreshold() {
		return Pair.of(reliabilitySpec, Threshold.greaterThanOrEqualTo(LOWER_THRESHOLD_REL));
	}

	private ExperienceSimulationRunner createPcmRelExperienceSimulationRunner(IProbabilityDistributionRegistry probabilityDistributionRegistry, IProbabilityDistributionFactory probabilityDistributionFactory, ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup) {
		return new PcmRelExperienceSimulationRunner(createDefaultReliabilityConfig(), probabilityDistributionRegistry, probabilityDistributionFactory, parameterParser, probDistRepoLookup);
	}
	
	private SimulatedMeasurementSpecification buildReliabilitySpec() {
		var usageScenario = experiment.getInitialModel().getUsageModel().getUsageScenario_UsageModel().get(0);
		return new PcmRelSimulatedMeasurementSpec(usageScenario);
	}
	
	private UncertaintyBasedReliabilityPredictionConfig createDefaultReliabilityConfig() {
		return new UncertaintyBasedReliabilityPredictionConfig(createDefaultRunConfig(),
				null, loadUncertaintyRepository(), null);
	}
	
	private UncertaintyRepository loadUncertaintyRepository() {
		var partition = new ResourceSetPartition();
		partition.loadModel(UNCERTAINTY_MODEL_URI);
		partition.resolveAllProxies();
		return (UncertaintyRepository) partition.getFirstContentElement(UNCERTAINTY_MODEL_URI);
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

}
