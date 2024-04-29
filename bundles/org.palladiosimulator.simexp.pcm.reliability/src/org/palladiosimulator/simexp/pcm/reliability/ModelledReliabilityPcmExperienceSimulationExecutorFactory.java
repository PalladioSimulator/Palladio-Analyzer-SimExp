package org.palladiosimulator.simexp.pcm.reliability;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.analyzer.workflow.ConstantsContainer;
import org.palladiosimulator.dependability.reliability.uncertainty.UncertaintyRepository;
import org.palladiosimulator.dependability.reliability.uncertainty.solver.api.UncertaintyBasedReliabilityPredictionConfig;
import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.evaluation.ExpectedRewardEvaluator;
import org.palladiosimulator.simexp.core.evaluation.TotalRewardCalculation;
import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.state.SimulationRunnerHolder;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.SmodelInterpreter;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mape.Monitor;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.mape.PcmMonitor;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.value.IModelsLookup;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.value.ModelsLookup;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.value.PcmProbeValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.value.EnvironmentVariableValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.model.strategy.ModelledReconfigurationStrategy;
import org.palladiosimulator.simexp.model.strategy.ModelledSimulationExecutor;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.modelled.ModelledModelLoader;
import org.palladiosimulator.simexp.pcm.modelled.simulator.ModelledPcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.modelled.simulator.config.IModelledPcmWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.reliability.entity.PcmRelSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.reliability.process.PcmRelExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;
import org.palladiosimulator.solver.runconfig.PCMSolverWorkflowRunConfiguration;

import de.uka.ipd.sdq.workflow.mdsd.blackboard.ResourceSetPartition;
import tools.mdsd.probdist.api.entity.CategoricalValue;
import tools.mdsd.probdist.api.parser.ParameterParser;

public class ModelledReliabilityPcmExperienceSimulationExecutorFactory
        extends ModelledPcmExperienceSimulationExecutorFactory<Double, List<InputValue<CategoricalValue>>> {

    // public static final double UPPER_THRESHOLD_RT = 0.1;
    // public static final double LOWER_THRESHOLD_REL = 0.9;

    public final static URI UNCERTAINTY_MODEL_URI = URI.createPlatformResourceURI(
            "/org.palladiosimulator.dependability.ml.hri/RobotCognitionUncertaintyModel.uncertainty", true);

    public ModelledReliabilityPcmExperienceSimulationExecutorFactory(
            IModelledPcmWorkflowConfiguration workflowConfiguration, ModelledModelLoader modelLoader,
            SimulatedExperienceStore<QVTOReconfigurator, Double> simulatedExperienceStore) {
        super(workflowConfiguration, modelLoader, simulatedExperienceStore);
    }

    @Override
    protected PcmExperienceSimulationExecutor<PCMInstance, QVTOReconfigurator, QVToReconfiguration, Double> doModelledCreate(
            Smodel smodel) {
        UncertaintyBasedReliabilityPredictionConfig predictionConfig = new UncertaintyBasedReliabilityPredictionConfig(
                createDefaultRunConfig(), null, loadUncertaintyRepository(), null);

        ParameterParser parameterParser = getParameterParser();
        List<ExperienceSimulationRunner> runners = List
            .of(new PcmRelExperienceSimulationRunner<>(predictionConfig, getProbabilityDistributionRegistry(),
                    getDistributionFactory(), parameterParser, getProbDistRepoLookup())
            /**
             * disabled PCM performance analysis based on SimuCom for RobotCognition example;
             * SimuCom is deprecated and simulation currently fails
             * 
             * , new PcmExperienceSimulationRunner(experimentProvider)
             */
            );

        Experiment experiment = loadExperiment();
        // FIXME: check if reconfigurationStrategy must be initialized
        IQVToReconfigurationManager qvtoReconfigurationManager = createQvtoReconfigurationManager(experiment);
        IExperimentProvider experimentProvider = createExperimentProvider(experiment);
        Initializable beforeExecutionInitializable = new RobotCognitionBeforeExecutionInitialization<>(null,
                experimentProvider, qvtoReconfigurationManager);

        RobotCognitionEnvironmentalDynamics<QVTOReconfigurator, Double> envDynamics = new RobotCognitionEnvironmentalDynamics<>(
                getDbn());
        EnvironmentProcess<QVTOReconfigurator, Double, List<InputValue<CategoricalValue>>> p = envDynamics
            .getEnvironmentProcess();
        EnvironmentProcess<QVTOReconfigurator, Double, List<InputValue<CategoricalValue>>> envProcess = p;

        UsageScenario usageScenario = experiment.getInitialModel()
            .getUsageModel()
            .getUsageScenario_UsageModel()
            .get(0);
        SimulatedMeasurementSpecification reliabilitySpec = new PcmRelSimulatedMeasurementSpec(usageScenario);
        List<PcmMeasurementSpecification> pcmMeasurementSpecs = createSpecs(experiment);
        List<SimulatedMeasurementSpecification> simSpecs = new ArrayList<>(pcmMeasurementSpecs);
        simSpecs.add(reliabilitySpec);

        List<SimulatedMeasurementSpecification> joinedSpecs = new ArrayList<>();
        joinedSpecs.addAll(pcmMeasurementSpecs); // currently contains the performance related
                                                 // measurement
        // specs
        // derived from monitorrepository model
        joinedSpecs.add(reliabilitySpec); // currently contains the reliability related measurement
                                          // specs derived from usage_scenario model

        IModelsLookup modelsLookup = new ModelsLookup(experiment);
        PcmProbeValueProvider probeValueProvider = new PcmProbeValueProvider(modelsLookup);
        EnvironmentVariableValueProvider environmentVariableValueProvider = new EnvironmentVariableValueProvider(
                probabilisticModelRepository);
        Monitor monitor = new PcmMonitor(simSpecs, probeValueProvider, environmentVariableValueProvider);
        SmodelInterpreter smodelInterpreter = new SmodelInterpreter(smodel, probeValueProvider,
                environmentVariableValueProvider);
        String reconfigurationStrategyId = smodel.getModelName();
        Policy<QVTOReconfigurator, QVToReconfiguration> reconfStrategy = new ModelledReconfigurationStrategy(
                reconfigurationStrategyId, monitor, smodelInterpreter, smodelInterpreter, qvtoReconfigurationManager);

        RewardEvaluator<Double> evaluator = new RealValuedRewardEvaluator(reliabilitySpec);

        Set<QVToReconfiguration> reconfigurations = new HashSet<>(qvtoReconfigurationManager.loadReconfigurations());

        SimulationRunnerHolder simulationRunnerHolder = createSimulationRunnerHolder();
        ExperienceSimulator<PCMInstance, QVTOReconfigurator, Double> experienceSimulator = createExperienceSimulator(
                experiment, joinedSpecs, runners, getSimulationParameters(), beforeExecutionInitializable, envProcess,
                getSimulatedExperienceStore(), null, reconfStrategy, reconfigurations, evaluator, true,
                experimentProvider, simulationRunnerHolder);

        String sampleSpaceId = SimulatedExperienceConstants
            .constructSampleSpaceId(getSimulationParameters().getSimulationID(), reconfigurationStrategyId);
        TotalRewardCalculation rewardCalculation = new ExpectedRewardEvaluator(
                getSimulationParameters().getSimulationID(), sampleSpaceId);

        ModelledSimulationExecutor<Double> executor = new ModelledSimulationExecutor<>(experienceSimulator, experiment,
                getSimulationParameters(), reconfStrategy, rewardCalculation, experimentProvider,
                qvtoReconfigurationManager);

        return executor;
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