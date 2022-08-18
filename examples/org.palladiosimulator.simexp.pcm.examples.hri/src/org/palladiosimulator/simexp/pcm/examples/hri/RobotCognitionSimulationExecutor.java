package org.palladiosimulator.simexp.pcm.examples.hri;

import java.util.Set;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.analyzer.workflow.ConstantsContainer;
import org.palladiosimulator.dependability.reliability.uncertainty.UncertaintyRepository;
import org.palladiosimulator.dependability.reliability.uncertainty.solver.api.UncertaintyBasedReliabilityPredictionConfig;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.monitorrepository.Monitor;
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
import org.palladiosimulator.simexp.core.strategy.mape.Analyzer;
import org.palladiosimulator.simexp.core.strategy.mape.Executer;
import org.palladiosimulator.simexp.core.strategy.mape.Planner;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.DummyProbeValueProvider;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.DummyVariableValueProvider;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.KmodelInterpreter;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.ProbeValueProvider;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.ProbeValueProviderMeasurementInjector;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.VariableValueProvider;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.RewardImpl;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.builder.PcmExperienceSimulationBuilder;
import org.palladiosimulator.simexp.pcm.examples.executor.KmodelSimulationExecutor;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.init.GlobalPcmBeforeExecutionInitialization;
import org.palladiosimulator.simexp.pcm.process.PcmExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.reliability.entity.PcmRelSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.reliability.process.PcmRelExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.solver.runconfig.PCMSolverWorkflowRunConfiguration;

import com.google.common.collect.Sets;

import de.uka.ipd.sdq.workflow.mdsd.blackboard.ResourceSetPartition;
import tools.mdsd.probdist.api.apache.supplier.MultinomialDistributionSupplier;
import tools.mdsd.probdist.api.apache.util.DistributionTypeModelUtil;
import tools.mdsd.probdist.api.factory.ProbabilityDistributionFactory;
import tools.mdsd.probdist.model.basic.loader.BasicDistributionTypesLoader;

public class RobotCognitionSimulationExecutor extends KmodelSimulationExecutor {
    
    private static final Logger LOGGER = Logger.getLogger(RobotCognitionSimulationExecutor.class.getName());

	public class RobotCognitionBeforeExecutionInitialization extends GlobalPcmBeforeExecutionInitialization {
		
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
	
	private final static String EXPERIMENT_FILE = "/org.palladiosimulator.dependability.ml.hri/RobotCognitionExperiment.experiments";
	private final static String SIMULATION_ID = "RobotCognition";
	private final static String RESPONSE_TIME_MONITOR = "System Response Time";
	private final static URI UNCERTAINTY_MODEL_URI = URI.createPlatformResourceURI("/org.palladiosimulator.dependability.ml.hri/RobotCognitionUncertaintyModel.uncertainty", true);
	
	private final DynamicBayesianNetwork dbn;
	private final SimulatedMeasurementSpecification responseTimeSpec;
	private final SimulatedMeasurementSpecification reliabilitySpec;
	private final ReconfigurationStrategy<?> reconfigurationStrategy;
	
	private RobotCognitionSimulationExecutor(Kmodel kmodel) {
	    super(kmodel);
		this.dbn = RobotCognitionDBNLoader.load();
		this.responseTimeSpec = buildResponseTimeSpec();
		this.reliabilitySpec = buildReliabilitySpec();
		//this.reconfigurationStrategy = new ReliabilityPrioritizedStrategy(responseTimeSpec);
		//this.reconfigurationStrategy = new RandomizedAdaptationStrategy(responseTimeSpec);
		
        DummyProbeValueProvider dummyPvp = new DummyProbeValueProvider();
        ProbeValueProvider pvp = dummyPvp;
        ProbeValueProviderMeasurementInjector pvpMeasurementInjector = dummyPvp;
        VariableValueProvider vvp = new DummyVariableValueProvider();
        KmodelInterpreter kmodelInterpreter = new KmodelInterpreter(kmodel, pvp, vvp);
        org.palladiosimulator.simexp.core.strategy.mape.Monitor monitor = null;
        Analyzer analyzer = kmodelInterpreter;
        Planner planner = kmodelInterpreter;
        Executer executer = null;
 
		this.reconfigurationStrategy = new StaticSystemSimulation(monitor, analyzer, planner, executer, responseTimeSpec, pvpMeasurementInjector);
		
		
		DistributionTypeModelUtil.get(BasicDistributionTypesLoader.loadRepository());
		ProbabilityDistributionFactory.get().register(new MultinomialDistributionSupplier());
	}
	
	
    public static final class RobotCognitionSimulationExecutorFactory {

        public RobotCognitionSimulationExecutorFactory() {
        }

        public RobotCognitionSimulationExecutor create(Kmodel kmodel) {
            return new RobotCognitionSimulationExecutor(kmodel);
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

	@Override
	protected String getExperimentFile() {
		return EXPERIMENT_FILE;
	}

	@Override
	protected ExperienceSimulator createSimulator() {
		return PcmExperienceSimulationBuilder.newBuilder()
				.makeGlobalPcmSettings()
					.withInitialExperiment(experiment)
					.andSimulatedMeasurementSpecs(Sets.newHashSet(responseTimeSpec, reliabilitySpec))
					.addExperienceSimulationRunner(createPcmRelExperienceSimulationRunner())
					.addExperienceSimulationRunner(new PcmExperienceSimulationRunner())
					.done()
				.createSimulationConfiguration()
					.withSimulationID(SIMULATION_ID)
					.withNumberOfRuns(5) //50
					.andNumberOfSimulationsPerRun(10) //100
					.andOptionalExecutionBeforeEachRun(new RobotCognitionBeforeExecutionInitialization())
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
		return Sets.newHashSet(QVToReconfigurationManager.get().loadReconfigurations());
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

	private ExperienceSimulationRunner createPcmRelExperienceSimulationRunner() {
		return new PcmRelExperienceSimulationRunner(createDefaultReliabilityConfig());
	}
	
	private SimulatedMeasurementSpecification buildReliabilitySpec() {
		var usageScenario = experiment.getInitialModel().getUsageModel().getUsageScenario_UsageModel().get(0);
		return new PcmRelSimulatedMeasurementSpec(usageScenario);
	}

	private SimulatedMeasurementSpecification buildResponseTimeSpec() {
		Monitor rtMonitor = findMonitor(RESPONSE_TIME_MONITOR);
		MeasurementSpecification rtSpec = rtMonitor.getMeasurementSpecifications().get(0);
		return PcmMeasurementSpecification.newBuilder()
				.withName(rtMonitor.getEntityName())
				.measuredAt(rtMonitor.getMeasuringPoint())
				.withMetric(rtSpec.getMetricDescription())
				.useDefaultMeasurementAggregation()
				.build();
	}
	
	private Monitor findMonitor(String monitorName) {
		Stream<Monitor> monitors = experiment.getInitialModel().getMonitorRepository().getMonitors().stream();
		return monitors.filter(m -> m.getEntityName().equals(monitorName))
					   .findFirst()
					   .orElseThrow(() -> new RuntimeException("There is no monitor."));
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
