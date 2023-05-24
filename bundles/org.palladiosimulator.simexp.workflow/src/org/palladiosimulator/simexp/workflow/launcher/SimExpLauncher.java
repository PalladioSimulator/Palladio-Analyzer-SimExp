package org.palladiosimulator.simexp.workflow.launcher;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.IntStream;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.palladiosimulator.analyzer.workflow.ConstantsContainer;
import org.palladiosimulator.analyzer.workflow.configurations.AbstractPCMLaunchConfigurationDelegate;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.dependability.reliability.uncertainty.UncertaintyRepository;
import org.palladiosimulator.dependability.reliability.uncertainty.solver.api.UncertaintyBasedReliabilityPredictionConfig;
import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.environment.dynamicmodel.DynamicBehaviourExtension;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundProbabilisticNetwork;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.experimentautomation.experiments.ExperimentRepository;
import org.palladiosimulator.pcm.query.RepositoryModelLookup;
import org.palladiosimulator.pcm.query.ResourceEnvironmentModelLookup;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.simexp.commons.constants.model.ModelFileTypeConstants;
import org.palladiosimulator.simexp.commons.constants.model.SimulationConstants;
import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.reward.ThresholdBasedRewardEvaluator;
import org.palladiosimulator.simexp.core.statespace.SelfAdaptiveSystemStateSpaceNavigator;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.model.io.DynamicBehaviourExtensionLoader;
import org.palladiosimulator.simexp.model.io.ExperimentRepositoryLoader;
import org.palladiosimulator.simexp.model.io.ExperimentRepositoryResolver;
import org.palladiosimulator.simexp.model.io.GroundProbabilisticNetworkLoader;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.builder.PcmExperienceSimulationBuilder;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.EnergyConsumptionPrismFileUpdater;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.GlobalQualityBasedReconfigurationStrategy;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.LocalQualityBasedReconfigurationStrategy;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.PacketLossPrismFileUpdater;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.TransmissionPowerReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.DeltaIoTEnvironemtalDynamics;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.DeltaIoTPcmBasedPrismExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.DeltaIoTReconfigurationParamsLoader;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.DeltaIoTSimulationExecutor;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.DistributionFactorReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.DeltaIoTSimulationExecutor.DeltaIoTSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.hri.RealValuedRewardEvaluator;
import org.palladiosimulator.simexp.pcm.examples.hri.RobotCognitionBeforeExecutionInitialization;
import org.palladiosimulator.simexp.pcm.examples.hri.RobotCognitionEnvironmentalDynamics;
import org.palladiosimulator.simexp.pcm.examples.hri.RobotCognitionSimulationExecutor;
import org.palladiosimulator.simexp.pcm.examples.hri.RobotCognitionSimulationExecutor.RobotCognitionSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.examples.hri.StaticSystemSimulation;
import org.palladiosimulator.simexp.pcm.examples.loadbalancing.LoadBalancingSimulationExecutor;
import org.palladiosimulator.simexp.pcm.examples.loadbalancing.LoadBalancingSimulationExecutor.LoadBalancingSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.examples.loadbalancing.NStepLoadBalancerStrategy;
import org.palladiosimulator.simexp.pcm.examples.loadbalancing.UsageScenarioToDBNTransformer;
import org.palladiosimulator.simexp.pcm.examples.loadbalancing.VaryingInterarrivelRateProcess;
import org.palladiosimulator.simexp.pcm.examples.performability.NodeRecoveryStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.PerformabilityRewardEvaluation;
import org.palladiosimulator.simexp.pcm.examples.performability.PerformabilityStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.PerformabilityStrategyConfiguration;
import org.palladiosimulator.simexp.pcm.examples.performability.RepositoryModelUpdater;
import org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing.FaultTolerantLoadBalancingSimulationExecutor;
import org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing.FaultTolerantScalingNodeFailureRecoveryStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing.FaultTolerantLoadBalancingSimulationExecutor.FaultTolerantLoadBalancingSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing.FaultTolerantVaryingInterarrivelRateProcess;
import org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing.LoadBalancingEmptyReconfigurationPlanningStrategy;
import org.palladiosimulator.simexp.pcm.init.GlobalPcmBeforeExecutionInitialization;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.prism.generator.PrismFileUpdateGenerator;
import org.palladiosimulator.simexp.pcm.prism.generator.PrismGenerator;
import org.palladiosimulator.simexp.pcm.prism.generator.PrismFileUpdateGenerator.PrismFileUpdater;
import org.palladiosimulator.simexp.pcm.process.PcmExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.process.PerformabilityPcmExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.reliability.entity.PcmRelSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.reliability.process.PcmRelExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simexp.pcm.util.SimulationParameterConfiguration;
import org.palladiosimulator.simexp.workflow.config.ArchitecturalModelsWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.config.EnvironmentalModelsWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.config.MonitorConfiguration;
import org.palladiosimulator.simexp.workflow.config.PrismConfiguration;
import org.palladiosimulator.simexp.workflow.config.SimExpWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.jobs.SimExpAnalyzerRootJob;
import org.palladiosimulator.simexp.workflow.provider.PcmMeasurementSpecificationProvider;
import org.palladiosimulator.simexp.workflow.provider.PrismMeasurementSpecificationProvider;
import org.palladiosimulator.solver.runconfig.PCMSolverWorkflowRunConfiguration;

import com.google.common.base.Objects;

import de.uka.ipd.sdq.workflow.jobs.IJob;
import de.uka.ipd.sdq.workflow.logging.console.LoggerAppenderStruct;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.ResourceSetPartition;
import tools.mdsd.probdist.api.apache.supplier.MultinomialDistributionSupplier;
import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.apache.util.ProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;
import tools.mdsd.probdist.api.factory.ProbabilityDistributionFactory;
import tools.mdsd.probdist.api.parser.DefaultParameterParser;
import tools.mdsd.probdist.api.parser.ParameterParser;
import tools.mdsd.probdist.distributiontype.ProbabilityDistributionRepository;
import tools.mdsd.probdist.model.basic.loader.BasicDistributionTypesLoader;

public class SimExpLauncher extends AbstractPCMLaunchConfigurationDelegate<SimExpWorkflowConfiguration> {

    private static final Logger LOGGER = Logger.getLogger(SimExpLauncher.class.getName());

    public SimExpLauncher() {

    }

    @Override
    protected IJob createWorkflowJob(SimExpWorkflowConfiguration config, ILaunch launch) throws CoreException {
        LOGGER.debug("Create SimExp workflow root job");
        try {
        	ResourceSet rs = new ResourceSetImpl();
        	
            URI experimentsFileURI = config.getExperimentsURI();
            ExperimentRepositoryLoader expLoader = new ExperimentRepositoryLoader();
            LOGGER.debug(String.format("Loading experiment from: '%s'", experimentsFileURI));
            ExperimentRepository experimentRepository = expLoader.load(rs, experimentsFileURI);
            
            ExperimentRepositoryResolver expRepoResolver = new ExperimentRepositoryResolver();
            Experiment experiment = expRepoResolver.resolveExperiment(experimentRepository);
            
            URI staticModelURI = config.getStaticModelURI();
            GroundProbabilisticNetworkLoader gpnLoader = new GroundProbabilisticNetworkLoader();
            LOGGER.debug(String.format("Loading static model from: '%s'", staticModelURI));
            GroundProbabilisticNetwork gpn = gpnLoader.load(rs, staticModelURI);
            
            URI dynamicModelURI = config.getDynamicModelURI();
            DynamicBehaviourExtensionLoader dbeLoader = new DynamicBehaviourExtensionLoader();
            LOGGER.debug(String.format("Loading dynamic model from: '%s'", dynamicModelURI));
            DynamicBehaviourExtension dbe = dbeLoader.load(rs, dynamicModelURI);
            
            ParameterParser parameterParser = new DefaultParameterParser();
            ProbabilityDistributionFactory defaultProbabilityDistributionFactory = new ProbabilityDistributionFactory();
            IProbabilityDistributionRegistry probabilityDistributionRegistry = defaultProbabilityDistributionFactory;
            IProbabilityDistributionFactory probabilityDistributionFactory = defaultProbabilityDistributionFactory;
            
            ProbabilityDistributionRepository probabilityDistributionRepository = BasicDistributionTypesLoader.loadRepository();
            IProbabilityDistributionRepositoryLookup probDistRepoLookup = new ProbabilityDistributionRepositoryLookup(probabilityDistributionRepository);

            BayesianNetwork bn = new BayesianNetwork(null, gpn, probabilityDistributionFactory);
            DynamicBayesianNetwork dbn = new DynamicBayesianNetwork(null, bn, dbe, probabilityDistributionFactory);
            
            SimulationExecutor simulationExecutor = createSimulationExecutor(config.getSimulationEngine(), config.getQualityObjective(), 
            		experiment, dbn, probabilityDistributionRegistry, probabilityDistributionFactory, parameterParser, probDistRepoLookup, 
            		config.getSimulationParameters(), config.getMonitorNames(), config.getPropertyFiles(), config.getModuleFiles());
            return new SimExpAnalyzerRootJob(config, simulationExecutor, launch);
        } catch (Exception e) {
            IStatus status = Status.error(e.getMessage(), e);
            throw new CoreException(status);
        }
    }

    @Override
    protected SimExpWorkflowConfiguration deriveConfiguration(ILaunchConfiguration configuration, String mode)
            throws CoreException {
        LOGGER.debug("Derive workflow configuration");
        return buildWorkflowConfiguration(configuration, mode);
    }
    
    @SuppressWarnings("unchecked")
    private SimulationExecutor createSimulationExecutor(String simulationEngine, String qualityObjective, Experiment experiment,
    		DynamicBayesianNetwork dbn, IProbabilityDistributionRegistry probabilityDistributionRegistry,
    		IProbabilityDistributionFactory probabilityDistributionFactory, ParameterParser parameterParser,
    		IProbabilityDistributionRepositoryLookup probDistRepoLookup, SimulationParameterConfiguration simulationParameters,
    		List<String> monitorNames, List<URI> propertyFiles, List<URI> moduleFiles) {
    	
    	probabilityDistributionRegistry.register(new MultinomialDistributionSupplier(parameterParser, probDistRepoLookup));
    	
    	return switch (simulationEngine) {
    		case SimulationConstants.SIMULATION_ENGINE_PCM ->{
    			PcmMeasurementSpecificationProvider provider = new PcmMeasurementSpecificationProvider(experiment);
    			List<PcmMeasurementSpecification> pcmSpecs = monitorNames
    		        		.stream()
    		        		.map(provider::getSpecification)
    		        		.toList();
    			
    			Set<Reconfiguration<?>> reconfigurations = new HashSet<Reconfiguration<?>>(QVToReconfigurationManager.get().loadReconfigurations());
    			 
    			yield switch (qualityObjective) {
     				case SimulationConstants.PERFORMANCE -> {
     					List<ExperienceSimulationRunner> simulationRunners = List.of(new PcmExperienceSimulationRunner());
     					Initializable beforeExecutionInitializable = new GlobalPcmBeforeExecutionInitialization();
     					Policy<Action<?>> reconfPolicy = new NStepLoadBalancerStrategy(2, pcmSpecs.get(0));
     					
     					Pair<SimulatedMeasurementSpecification, Threshold> threshold = Pair.of(pcmSpecs.get(0), 
     							Threshold.lessThanOrEqualTo(LoadBalancingSimulationExecutor.UPPER_THRESHOLD_RT));
     					RewardEvaluator evaluator = ThresholdBasedRewardEvaluator.with(threshold);
     					
     					boolean simulateWithUsageEvolution = true; // FIXME
     					EnvironmentProcess envProcess;
     					if (simulateWithUsageEvolution) {
     						var usage = experiment.getInitialModel().getUsageEvolution().getUsages().get(0);
     						var dynBehaviour = new UsageScenarioToDBNTransformer().transformAndPersist(usage);
     						var bn = new BayesianNetwork(null, dynBehaviour.getModel(), probabilityDistributionFactory);
     						envProcess = VaryingInterarrivelRateProcess.get(new DynamicBayesianNetwork(null, bn, dynBehaviour, probabilityDistributionFactory));
     					} else {
     					    envProcess = VaryingInterarrivelRateProcess.get(dbn);
     					}
     					
     					ExperienceSimulator simulator = createExperienceSimulator(experiment, pcmSpecs, simulationRunners, 
     							simulationParameters, beforeExecutionInitializable, envProcess, null, reconfPolicy, reconfigurations, evaluator, false);
     					
     					LoadBalancingSimulationExecutorFactory factory = new LoadBalancingSimulationExecutorFactory();
     					yield factory.create(simulator, experiment, simulationParameters, reconfPolicy);
     				}
     			
     				case SimulationConstants.RELIABILITY -> {
     					UsageScenario usageScenario = experiment.getInitialModel().getUsageModel().getUsageScenario_UsageModel().get(0);
     					SimulatedMeasurementSpecification reliabilitySpec = new PcmRelSimulatedMeasurementSpec(usageScenario);
     					List<SimulatedMeasurementSpecification> specs = new ArrayList<>(pcmSpecs);
     					specs.add(reliabilitySpec);
     					
     					UncertaintyBasedReliabilityPredictionConfig predictionConfig = new UncertaintyBasedReliabilityPredictionConfig(createDefaultRunConfig(), null, loadUncertaintyRepository(), null);
     					List<ExperienceSimulationRunner> runners = List.of(new PcmRelExperienceSimulationRunner(predictionConfig, 
     							probabilityDistributionRegistry, probabilityDistributionFactory, parameterParser, probDistRepoLookup));
     					
     					ReconfigurationStrategy<? extends Reconfiguration<?>> reconfStrategy = new StaticSystemSimulation();
     					Initializable beforeExecutionInitializable = new RobotCognitionBeforeExecutionInitialization(reconfStrategy);
     					EnvironmentProcess envProcess = RobotCognitionEnvironmentalDynamics.get(dbn);
     					RewardEvaluator evaluator = new RealValuedRewardEvaluator(reliabilitySpec);
     					
     					ExperienceSimulator simulator = createExperienceSimulator(experiment, specs, runners, simulationParameters, 
     							beforeExecutionInitializable, envProcess, null, (Policy<Action<?>>) reconfStrategy, reconfigurations, evaluator, true);
     					
     					RobotCognitionSimulationExecutorFactory factory = new RobotCognitionSimulationExecutorFactory();
     					yield factory.create(simulator, experiment,  simulationParameters, reconfStrategy);
     				}
     			
     				case SimulationConstants.PERFORMABILITY -> {
     					List<ExperienceSimulationRunner> runners = List.of(new PerformabilityPcmExperienceSimulationRunner());
     					Initializable beforeExecutionInitializable = new GlobalPcmBeforeExecutionInitialization();
     					EnvironmentProcess envProcess = FaultTolerantVaryingInterarrivelRateProcess.get(dbn);
     					
     					Pair<SimulatedMeasurementSpecification, Threshold> upperThresh = Pair.of(pcmSpecs.get(0), 
     							Threshold.lessThanOrEqualTo(FaultTolerantLoadBalancingSimulationExecutor.UPPER_THRESHOLD_RT.getValue()));
     					Pair<SimulatedMeasurementSpecification, Threshold> lowerThresh = Pair.of(pcmSpecs.get(0), 
     							Threshold.lessThanOrEqualTo(FaultTolerantLoadBalancingSimulationExecutor.LOWER_THRESHOLD_RT.getValue()));
     					RewardEvaluator evaluator = new PerformabilityRewardEvaluation(pcmSpecs.get(0), pcmSpecs.get(1), upperThresh, lowerThresh);
     					
     					PerformabilityStrategyConfiguration config = new PerformabilityStrategyConfiguration(FaultTolerantLoadBalancingSimulationExecutor.SERVER_FAILURE_TEMPLATE_ID, 
     							FaultTolerantLoadBalancingSimulationExecutor.LOAD_BALANCER_ID);
     					NodeRecoveryStrategy strategy = new FaultTolerantScalingNodeFailureRecoveryStrategy(config, new RepositoryModelLookup()
     					        , new ResourceEnvironmentModelLookup(), new RepositoryModelUpdater());
     					LoadBalancingEmptyReconfigurationPlanningStrategy reconfStrategy = new LoadBalancingEmptyReconfigurationPlanningStrategy(pcmSpecs.get(0), config, strategy);
     					ReconfigurationStrategy<? extends Reconfiguration<?>> reconfSelectionPolicy = new PerformabilityStrategy(pcmSpecs.get(0), config, reconfStrategy);
     					
     					ExperienceSimulator simulator = createExperienceSimulator(experiment, pcmSpecs, runners, simulationParameters, 
     							beforeExecutionInitializable, envProcess, null, (Policy<Action<?>>) reconfSelectionPolicy, reconfigurations, evaluator, false);
     					
     					FaultTolerantLoadBalancingSimulationExecutorFactory factory = new FaultTolerantLoadBalancingSimulationExecutorFactory();
     					yield factory.create(simulator, experiment, simulationParameters, reconfSelectionPolicy);
     				}
     			
     				default -> throw new RuntimeException("Unexpected quality objective " + qualityObjective);
    			};
    		}
    		
    		case SimulationConstants.SIMULATION_ENGINE_PRISM -> {
    			PrismMeasurementSpecificationProvider provider = new PrismMeasurementSpecificationProvider();
    			List<PrismSimulatedMeasurementSpec> prismSpecs = IntStream
    					.range(0, Math.min(propertyFiles.size(), moduleFiles.size()))
    					.mapToObj(i -> provider.getSpecification(propertyFiles.get(i), moduleFiles.get(i)))
    					.toList();
    			
    			URI uri = URI.createPlatformResourceURI(Paths.get(DeltaIoTSimulationExecutor.DELTAIOT_PATH, DeltaIoTSimulationExecutor.PRISM_FOLDER).toString(), true);
    			File prismLogFile = new File(CommonPlugin.resolve(uri).toFileString());
    			
    			Set<PrismFileUpdater> prismFileUpdaters = new HashSet<>();
    			prismFileUpdaters.add(new PacketLossPrismFileUpdater(prismSpecs.get(0)));
    			prismFileUpdaters.add(new EnergyConsumptionPrismFileUpdater(prismSpecs.get(1)));
    			PrismGenerator prismGenerator = new PrismFileUpdateGenerator(prismFileUpdaters);
    			
    			DeltaIoTReconfigurationParamRepository reconfParamsRepo = new DeltaIoTReconfigurationParamsLoader().load(DeltaIoTSimulationExecutor.DISTRIBUTION_FACTORS);
    			QVToReconfigurationManager.get().addModelsToTransform(reconfParamsRepo.eResource());
    			
    			ExperienceSimulationRunner runner = new DeltaIoTPcmBasedPrismExperienceSimulationRunner(prismGenerator, prismLogFile, reconfParamsRepo);
    			Initializable beforeExecutionInitializable = new GlobalPcmBeforeExecutionInitialization();
    			SelfAdaptiveSystemStateSpaceNavigator navigator = DeltaIoTEnvironemtalDynamics.getPartiallyEnvironmentalDrivenProcess(dbn);
    			
    			Policy<Action<?>> reconfSelectionPolicy =  LocalQualityBasedReconfigurationStrategy.newBuilder()
    					.withReconfigurationParams(reconfParamsRepo)
    					.andPacketLossSpec(prismSpecs.get(0))
    					.andEnergyConsumptionSpec(prismSpecs.get(1))
    					.build();
    			
    			Pair<SimulatedMeasurementSpecification, Threshold> lowerPacketLossThreshold = Pair.of(prismSpecs.get(0), GlobalQualityBasedReconfigurationStrategy.LOWER_PACKET_LOSS);
    			Pair<SimulatedMeasurementSpecification, Threshold> lowerEnergyConsumptionThreshold = Pair.of(prismSpecs.get(1), GlobalQualityBasedReconfigurationStrategy.LOWER_ENERGY_CONSUMPTION);
    			RewardEvaluator evaluator = ThresholdBasedRewardEvaluator.with(lowerPacketLossThreshold, lowerEnergyConsumptionThreshold);
    			
    			Set<Reconfiguration<?>> reconfigurations = new HashSet<>();
    			QVToReconfigurationManager.get().loadReconfigurations().stream()
    				.forEach(qvto -> {
    					if (DistributionFactorReconfiguration.isCorrectQvtReconfguration(qvto)) {
    						reconfigurations.add(new DistributionFactorReconfiguration(qvto, reconfParamsRepo.getDistributionFactors()));
    					} else if (TransmissionPowerReconfiguration.isCorrectQvtReconfguration(qvto)) {
    						reconfigurations.add(new TransmissionPowerReconfiguration(qvto, reconfParamsRepo.getTransmissionPower()));
    					}
    			});
    			
    			ExperienceSimulator simulator = createExperienceSimulator(experiment, prismSpecs, List.of(runner), simulationParameters, 
    					beforeExecutionInitializable, null, navigator, reconfSelectionPolicy,  reconfigurations, evaluator, false);
    			
    			DeltaIoTSimulationExecutorFactory factory = new DeltaIoTSimulationExecutorFactory();
    			yield factory.create(simulator, experiment, simulationParameters, reconfSelectionPolicy);
    		}
    		
    		default -> throw new RuntimeException("Unexpected simulation engine " + simulationEngine);
    	};
    }
    
    private ExperienceSimulator createExperienceSimulator(Experiment experiment, List<? extends SimulatedMeasurementSpecification> specs, 
    		List<ExperienceSimulationRunner> runners, SimulationParameterConfiguration params, Initializable beforeExecution, EnvironmentProcess envProcess,
    		SelfAdaptiveSystemStateSpaceNavigator navigator, Policy<Action<?>> reconfStrategy, Set<Reconfiguration<?>> reconfigurations, RewardEvaluator evaluator, boolean hidden) {
    	
    	return PcmExperienceSimulationBuilder.newBuilder()
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
		partition.loadModel(RobotCognitionSimulationExecutor.UNCERTAINTY_MODEL_URI);
		partition.resolveAllProxies();
		return (UncertaintyRepository) partition.getFirstContentElement(RobotCognitionSimulationExecutor.UNCERTAINTY_MODEL_URI);
	}
    
    @SuppressWarnings("unchecked")
    private SimExpWorkflowConfiguration buildWorkflowConfiguration(ILaunchConfiguration configuration, String mode) {
        SimExpWorkflowConfiguration workflowConfiguration = null;
        try {
            Map<String, Object> launchConfigurationParams = configuration.getAttributes();

            if (LOGGER.isDebugEnabled()) {
                for (Entry<String, Object> entry : launchConfigurationParams.entrySet()) {
                    LOGGER.debug(
                            String.format("launch configuration param ['%s':'%s']", entry.getKey(), entry.getValue()));
                }
            }
            
            String simulationEngine = (String) launchConfigurationParams.get(SimulationConstants.SIMULATION_ENGINE);
            
            SimulationParameterConfiguration simulationParameters = new SimulationParameterConfiguration(
            		(String) launchConfigurationParams.get(SimulationConstants.SIMULATION_ID),
            		(int) launchConfigurationParams.get(SimulationConstants.NUMBER_OF_RUNS),
            		(int) launchConfigurationParams.get(SimulationConstants.NUMBER_OF_SIMULATIONS_PER_RUN));

            ArchitecturalModelsWorkflowConfiguration architecturalModels = new ArchitecturalModelsWorkflowConfiguration(
            		Arrays.asList((String) launchConfigurationParams.get(ModelFileTypeConstants.ALLOCATION_FILE)),
            		(String) launchConfigurationParams.get(ModelFileTypeConstants.USAGE_FILE),
            		(String) launchConfigurationParams.get(ModelFileTypeConstants.EXPERIMENTS_FILE));
            
            EnvironmentalModelsWorkflowConfiguration environmentalModels = new EnvironmentalModelsWorkflowConfiguration(
            		(String) launchConfigurationParams.get(ModelFileTypeConstants.STATIC_MODEL_FILE),
                    (String) launchConfigurationParams.get(ModelFileTypeConstants.DYNAMIC_MODEL_FILE));

            /** simulation type = PCM */
            String qualityObjective = StringUtils.EMPTY;
            String monitorRepositoryFile = StringUtils.EMPTY;
            List<String> configuredMonitors = new ArrayList<String>();
            if (Objects.equal(SimulationConstants.SIMULATION_ENGINE_PCM, simulationEngine)) {
	            qualityObjective = (String) launchConfigurationParams.get(SimulationConstants.QUALITY_OBJECTIVE);
	            
	            monitorRepositoryFile = (String) launchConfigurationParams.get(ModelFileTypeConstants.MONITOR_REPOSITORY_FILE);
        		configuredMonitors.addAll((List<String>) launchConfigurationParams.get(ModelFileTypeConstants.MONITORS));
            }
	            
            MonitorConfiguration monitors = new MonitorConfiguration(monitorRepositoryFile, configuredMonitors);

            /** simulation type = PRISM */
            List<String> prismProperties = new ArrayList<String>();
            List<String> prismModules = new ArrayList<String>();
            if (Objects.equal(SimulationConstants.SIMULATION_ENGINE_PRISM, simulationEngine)) {
            	List<String> launchConfigPrismProperties = (List<String>) launchConfigurationParams.get(ModelFileTypeConstants.PRISM_PROPERTY_FILE);
            	List<String> launchConfigModulesProperties = (List<String>) launchConfigurationParams.get(ModelFileTypeConstants.PRISM_MODULE_FILE);
            	prismProperties.addAll(launchConfigPrismProperties);
            	prismModules.addAll(launchConfigModulesProperties);
            }
            PrismConfiguration prismConfig = new PrismConfiguration(prismProperties, prismModules);
            
            
            /** FIXME: split workflow configuraiton based on simulation type: PCM, PRISM */
            workflowConfiguration = new SimExpWorkflowConfiguration(simulationEngine, qualityObjective, architecturalModels, monitors, prismConfig, 
            		environmentalModels, simulationParameters);
        } catch (CoreException e) {
            LOGGER.error("Failed to read workflow configuration from passed launch configuration. Please check the provided launch configuration", e);
        }

        return workflowConfiguration;
    }

    @Override
    protected ArrayList<LoggerAppenderStruct> setupLogging(Level logLevel) throws CoreException {
        // FIXME: during development set debug level hard-coded to DEBUG
        ArrayList<LoggerAppenderStruct> loggerList = super.setupLogging(Level.DEBUG);
        loggerList.add(setupLogger("org.palladiosimulator.simexp", logLevel,
                Level.DEBUG == logLevel ? DETAILED_LOG_PATTERN : SHORT_LOG_PATTERN));
        loggerList.add(setupLogger("org.palladiosimulator.experimentautomation.application", logLevel,
                Level.DEBUG == logLevel ? DETAILED_LOG_PATTERN : SHORT_LOG_PATTERN));
        loggerList.add(setupLogger("org.palladiosimulator.simulizar.reconfiguration.qvto", logLevel,
                Level.DEBUG == logLevel ? DETAILED_LOG_PATTERN : SHORT_LOG_PATTERN));
        return loggerList;
    }

}
