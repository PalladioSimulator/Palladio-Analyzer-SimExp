package org.palladiosimulator.simexp.pcm.examples.udacitychallenge.reliability;

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
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.state.StateQuantity;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.RewardImpl;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.init.GlobalPcmBeforeExecutionInitialization;
import org.palladiosimulator.simexp.pcm.reliability.entity.PcmRelSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.util.SimulationParameterConfiguration;
import org.palladiosimulator.solver.runconfig.PCMSolverWorkflowRunConfiguration;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.uka.ipd.sdq.workflow.mdsd.blackboard.ResourceSetPartition;
import tools.mdsd.probdist.api.apache.supplier.MultinomialDistributionSupplier;
import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;
import tools.mdsd.probdist.api.parser.ParameterParser;

public class UdacitySimExpExecutor extends PcmExperienceSimulationExecutor {

	public class UdcityBeforeExecutionInitialization extends GlobalPcmBeforeExecutionInitialization {
		
		@Override
		public void initialize() {
			super.initialize();
			
			if (reconfigurationStrategy instanceof Initializable) {
				Initializable.class.cast(reconfigurationStrategy).initialize();
			}
		}
		
	}
	
	private final static Logger LOGGER = Logger.getLogger(UdacitySimExpExecutor.class.getName());
	
	private final static String SIMULATION_ID = "UdacityChallenge";
	private final static URI UNCERTAINTY_MODEL_URI = URI.createPlatformResourceURI("org.palladiosimulator.simexp.pcm.examples.udacitychallenge/SteeringAnglePredictionUncertaintyModel.uncertainty", true);
	
	private final DynamicBayesianNetwork dbn;
	private final List<SimulatedMeasurementSpecification> pcmSpecs;
	private final ReconfigurationStrategy<? extends Reconfiguration<?>> reconfigurationStrategy;
	
	private final IProbabilityDistributionRegistry probabilityDistributionRegistry;
	private final IProbabilityDistributionFactory probabilityDistributionFactory;
	private final ParameterParser parameterParser;
	private final IProbabilityDistributionRepositoryLookup probDistRepoLookup;
	
	private UdacitySimExpExecutor(ExperienceSimulator experienceSimulator, Experiment experiment, DynamicBayesianNetwork dbn, IProbabilityDistributionRegistry probabilityDistributionRegistry, IProbabilityDistributionFactory probabilityDistributionFactory, ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup, SimulationParameterConfiguration simulationParameters) {
		super(experienceSimulator, experiment, simulationParameters);
		this.dbn = dbn;
		this.probabilityDistributionRegistry = probabilityDistributionRegistry;
		this.probabilityDistributionFactory = probabilityDistributionFactory;
		this.parameterParser = parameterParser;
		this.probDistRepoLookup = probDistRepoLookup;
		this.pcmSpecs = createSimMeasurementSpecs();
		//this.reconfigurationStrategy = new ImageBlurMitigationStrategy();
		this.reconfigurationStrategy = new RandomizedFilterActivationStrategy();
		//this.reconfigurationStrategy = new StaticSystemSimulation();
		
		probabilityDistributionRegistry.register(new MultinomialDistributionSupplier(parameterParser, probDistRepoLookup));
	}
	
	public static final class UdacitySimExpExecutorFactory {
	    public UdacitySimExpExecutor create(ExperienceSimulator experienceSimulator, Experiment experiment, DynamicBayesianNetwork dbn, IProbabilityDistributionRegistry probabilityDistributionRegistry, IProbabilityDistributionFactory probabilityDistributionFactory, ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup, SimulationParameterConfiguration simulationParameters) {
	        return new UdacitySimExpExecutor(experienceSimulator, experiment, dbn, probabilityDistributionRegistry, probabilityDistributionFactory, parameterParser, probDistRepoLookup, simulationParameters);
	    }
	}
	
	@Override
	public void evaluate() {
		String sampleSpaceId = SimulatedExperienceConstants.constructSampleSpaceId(SIMULATION_ID, reconfigurationStrategy.getId());
		TotalRewardCalculation evaluator = new ExpectedRewardEvaluator(SIMULATION_ID, sampleSpaceId);
		
		LOGGER.info("***********************************************************************");
		LOGGER.info(String.format("The total Reward of policy %1s is %2s", reconfigurationStrategy.getId(), evaluator.computeTotalReward()));
		LOGGER.info("***********************************************************************");
	}

/*
	@Override
	@SuppressWarnings("unchecked")
	protected ExperienceSimulator createSimulator() {
		return PcmExperienceSimulationBuilder.newBuilder()
				.makeGlobalPcmSettings()
					.withInitialExperiment(experiment)
					.andSimulatedMeasurementSpecs(Sets.newHashSet(pcmSpecs))
					.addExperienceSimulationRunner(new PcmRelExperienceSimulationRunner(createDefaultReliabilityConfig(), probabilityDistributionRegistry, probabilityDistributionFactory, parameterParser, probDistRepoLookup))
					.done()
				.createSimulationConfiguration()
					.withSimulationID(SIMULATION_ID)
					.withNumberOfRuns(50) //50
					.andNumberOfSimulationsPerRun(100) //100
					.andOptionalExecutionBeforeEachRun(new UdcityBeforeExecutionInitialization())
					.done()
				.specifySelfAdaptiveSystemState()
				  	.asEnvironmentalDrivenProcess(UdacityEnvironmentalDynamics.get(dbn))
					.done()
				.createReconfigurationSpace()
					.addReconfigurations(getAllReconfigurations())
				  	.andReconfigurationStrategy((Policy<Action<?>>) reconfigurationStrategy)
				  	.done()
				.specifyRewardHandling()
				  	.withRewardEvaluator(getRewardEvaluator())
				  	.done()
				.build();
	}
	*/
	
	private RewardEvaluator getRewardEvaluator() {
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
				var reliability = quantifiedState.findMeasurementWith(pcmSpecs.get(0)).get().getValue();
				return new RealValuedReward(reliability);
			}
		};
	}

	private Set<Reconfiguration<?>> getAllReconfigurations() {
		return Sets.newHashSet(QVToReconfigurationManager.get().loadReconfigurations());
	}
	
	private List<SimulatedMeasurementSpecification> createSimMeasurementSpecs() {
		return Lists.newArrayList(buildReliabilitySpec());
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
        
        config.setLogFile(null);
        config.setNumberOfEvaluatedSystemStatesEnabled(false);
        config.setNumberOfEvaluatedSystemStates(0);
        config.setNumberOfExactDecimalPlacesEnabled(false);
        config.setNumberOfExactDecimalPlaces(0);
        config.setSolvingTimeLimitEnabled(false);
        config.setMarkovModelStorageEnabled(false);
        config.setIterationOverPhysicalSystemStatesEnabled(true);
        config.setMarkovEvaluationMode("POINTSOFFAILURE"); 
        config.setSaveResultsToFileEnabled(false);
        
        config.setRMIMiddlewareFile(ConstantsContainer.DEFAULT_RMI_MIDDLEWARE_REPOSITORY_FILE);
        config.setEventMiddlewareFile(ConstantsContainer.DEFAULT_EVENT_MIDDLEWARE_FILE);
        return config;
	}

}
