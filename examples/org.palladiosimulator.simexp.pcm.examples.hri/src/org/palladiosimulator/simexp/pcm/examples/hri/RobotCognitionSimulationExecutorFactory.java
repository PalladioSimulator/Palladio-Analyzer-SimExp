package org.palladiosimulator.simexp.pcm.examples.hri;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.analyzer.workflow.core.ConstantsContainer;
import org.palladiosimulator.dependability.reliability.uncertainty.UncertaintyRepository;
import org.palladiosimulator.dependability.reliability.uncertainty.solver.api.UncertaintyBasedReliabilityPredictionConfig;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.evaluation.TotalRewardCalculation;
import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator;
import org.palladiosimulator.simexp.core.state.SimulationRunnerHolder;
import org.palladiosimulator.simexp.core.store.ISimulatedExperienceAccessor;
import org.palladiosimulator.simexp.core.store.ISimulatedExperienceStore;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationProvider;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.reliability.RealValuedRewardEvaluator;
import org.palladiosimulator.simexp.pcm.reliability.RobotCognitionBeforeExecutionInitialization;
import org.palladiosimulator.simexp.pcm.reliability.RobotCognitionEnvironmentalDynamics;
import org.palladiosimulator.simexp.pcm.reliability.StaticSystemSimulation;
import org.palladiosimulator.simexp.pcm.reliability.entity.PcmRelSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.reliability.process.PcmRelExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.simulator.SimulatorPcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.simulator.config.IPCMWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.core.models.PCMInstance;
import org.palladiosimulator.solver.core.runconfig.PCMSolverWorkflowRunConfiguration;

import de.uka.ipd.sdq.workflow.mdsd.blackboard.ResourceSetPartition;
import tools.mdsd.probdist.api.entity.CategoricalValue;
import tools.mdsd.probdist.api.parser.ParameterParser;
import tools.mdsd.probdist.api.random.ISeedProvider;

public class RobotCognitionSimulationExecutorFactory
        extends SimulatorPcmExperienceSimulationExecutorFactory<Double, List<InputValue<CategoricalValue>>> {
    public static final double UPPER_THRESHOLD_RT = 0.1;
    public static final double LOWER_THRESHOLD_REL = 0.9;

    public final static URI UNCERTAINTY_MODEL_URI = URI.createPlatformResourceURI(
            "/org.palladiosimulator.dependability.ml.hri/RobotCognitionUncertaintyModel.uncertainty", true);

    public RobotCognitionSimulationExecutorFactory(IPCMWorkflowConfiguration workflowConfiguration,
            ModelLoader.Factory modelLoaderFactory,
            ISimulatedExperienceStore<QVTOReconfigurator, Double> simulatedExperienceStore,
            Optional<ISeedProvider> seedProvider, ISimulatedExperienceAccessor accessor, Path resourcePath) {
        super(workflowConfiguration, modelLoaderFactory, simulatedExperienceStore, seedProvider, accessor,
                resourcePath);
    }

    @Override
    protected PcmExperienceSimulationExecutor<PCMInstance, QVTOReconfigurator, QVToReconfiguration, Double> doCreate(
            Experiment experiment, DynamicBayesianNetwork<CategoricalValue> dbn) {
        UsageScenario usageScenario = experiment.getInitialModel()
            .getUsageModel()
            .getUsageScenario_UsageModel()
            .get(0);
        SimulatedMeasurementSpecification reliabilitySpec = new PcmRelSimulatedMeasurementSpec(usageScenario);
        List<PcmMeasurementSpecification> pcmMeasurementSpecs = createSpecs(experiment);
        List<SimulatedMeasurementSpecification> relSpecs = new ArrayList<>(pcmMeasurementSpecs);
        relSpecs.add(reliabilitySpec);

        List<SimulatedMeasurementSpecification> joinedSpecs = new ArrayList<>();
        joinedSpecs.addAll(pcmMeasurementSpecs); // currently contains the performance related
                                                 // measurement
        // specs
        // derived from monitorrepository model
        joinedSpecs.add(reliabilitySpec); // currently contains the reliability related measurement
                                          // specs derived from usage_scenario model

        UncertaintyBasedReliabilityPredictionConfig predictionConfig = new UncertaintyBasedReliabilityPredictionConfig(
                createDefaultRunConfig(), null, loadUncertaintyRepository(), null);
        ParameterParser parameterParser = getParameterParser();
        List<ExperienceSimulationRunner> runners = List
            .of(new PcmRelExperienceSimulationRunner<>(predictionConfig, getProbabilityDistributionRegistry(),
                    getDistributionFactory(), parameterParser, getProbDistRepoLookup(), getSeedProvider())
            /**
             * disabled PCM performance analysis based on SimuCom for RobotCognition example;
             * SimuCom is deprecated and simulation currently fails
             * 
             * , new PcmExperienceSimulationRunner(experimentProvider)
             */
            );

        ReconfigurationStrategy<QVTOReconfigurator, QVToReconfiguration> reconfSelectionPolicy = new StaticSystemSimulation();
        IQVToReconfigurationManager qvtoReconfigurationManager = createQvtoReconfigurationManager(experiment,
                getWorkflowConfiguration());
        IExperimentProvider experimentProvider = createExperimentProvider(experiment);
        List<Initializable> beforeExecutionInitializables = new ArrayList<>();
        Initializable beforeExecutionInitializable = new RobotCognitionBeforeExecutionInitialization<>(
                reconfSelectionPolicy, experimentProvider, qvtoReconfigurationManager);
        beforeExecutionInitializables.add(beforeExecutionInitializable);

        RobotCognitionEnvironmentalDynamics<QVTOReconfigurator, Double> envDynamics = new RobotCognitionEnvironmentalDynamics<>(
                dbn);
        envDynamics.init(getSeedProvider());
        EnvironmentProcess<QVTOReconfigurator, Double, List<InputValue<CategoricalValue>>> p = envDynamics
            .getEnvironmentProcess();
        EnvironmentProcess<QVTOReconfigurator, Double, List<InputValue<CategoricalValue>>> envProcess = p;

        RewardEvaluator<Double> evaluator = new RealValuedRewardEvaluator(reliabilitySpec);

        IQVToReconfigurationProvider qvToReconfigurationProvider = qvtoReconfigurationManager
            .getQVToReconfigurationProvider();
        Set<QVToReconfiguration> reconfigurations = qvToReconfigurationProvider.getReconfigurations();

        SimulationRunnerHolder simulationRunnerHolder = createSimulationRunnerHolder();
        ExperienceSimulator<PCMInstance, QVTOReconfigurator, Double> simulator = createExperienceSimulator(experiment,
                joinedSpecs, runners, getSimulationParameters(), beforeExecutionInitializables, envProcess,
                getSimulatedExperienceStore(), null, reconfSelectionPolicy, reconfigurations, evaluator, true,
                experimentProvider, simulationRunnerHolder, null, getSeedProvider());

        TotalRewardCalculation rewardCalculation = createRewardCalculation(reconfSelectionPolicy.getId());
        IQualityEvaluator qualityEvaluator = createQualityEvaluator(pcmMeasurementSpecs);

        return new PcmExperienceSimulationExecutor<>(simulator, experiment, getSimulationParameters(),
                reconfSelectionPolicy, rewardCalculation, qualityEvaluator, experimentProvider);
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
