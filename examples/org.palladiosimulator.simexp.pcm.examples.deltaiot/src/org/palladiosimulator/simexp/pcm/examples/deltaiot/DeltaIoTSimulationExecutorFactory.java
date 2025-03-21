package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.evaluation.ExpectedRewardEvaluator;
import org.palladiosimulator.simexp.core.evaluation.TotalRewardCalculation;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.state.SimulationRunnerHolder;
import org.palladiosimulator.simexp.core.statespace.SelfAdaptiveSystemStateSpaceNavigator;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationProvider;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.config.IPrismWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.process.DeltaIoTPcmBasedPrismExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.process.EnergyConsumptionPrismFileUpdater;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.process.PacketLossPrismFileUpdater;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.provider.PrismMeasurementSpecificationProvider;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.DeltaIoToReconfCustomizerFactory;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.IDeltaIoToReconfCustomizerFactory;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reward.QualityBasedRewardEvaluator;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.DeltaIoTDefaultReconfigurationStrategy;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTModelAccess;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTReconfigurationParamsLoader;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIotCSVSystemConfigurationStatisticSink;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.SystemConfigurationTracker;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.init.GlobalPcmBeforeExecutionInitialization;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.prism.generator.PrismFileUpdateGenerator;
import org.palladiosimulator.simexp.pcm.prism.generator.PrismFileUpdateGenerator.PrismFileUpdater;
import org.palladiosimulator.simexp.pcm.prism.generator.PrismGenerator;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.core.models.PCMInstance;

import tools.mdsd.probdist.api.entity.CategoricalValue;
import tools.mdsd.probdist.api.random.ISeedProvider;

public class DeltaIoTSimulationExecutorFactory extends
        PcmExperienceSimulationExecutorFactory<Double, List<InputValue<CategoricalValue>>, PrismSimulatedMeasurementSpec> {
    private static final Logger LOGGER = Logger.getLogger(DeltaIoTSimulationExecutorFactory.class);
    public final static String DELTAIOT_PATH = "/org.palladiosimulator.envdyn.examples.deltaiot";
    public final static String DISTRIBUTION_FACTORS = DELTAIOT_PATH
            + "/model/DeltaIoTReconfigurationParams.reconfigurationparams";

    public DeltaIoTSimulationExecutorFactory(IPrismWorkflowConfiguration workflowConfiguration,
            ModelLoader.Factory modelLoaderFactory,
            SimulatedExperienceStore<QVTOReconfigurator, Double> simulatedExperienceStore,
            Optional<ISeedProvider> seedProvider) {
        super(workflowConfiguration, modelLoaderFactory, simulatedExperienceStore, seedProvider);
    }

    @Override
    protected IPrismWorkflowConfiguration getWorkflowConfiguration() {
        return (IPrismWorkflowConfiguration) super.getWorkflowConfiguration();
    }

    @Override
    protected List<PrismSimulatedMeasurementSpec> createSpecs(Experiment experiment) {
        PrismMeasurementSpecificationProvider provider = new PrismMeasurementSpecificationProvider(
                getWorkflowConfiguration());
        List<PrismSimulatedMeasurementSpec> prismSpecs = provider.getSpecifications();
        return prismSpecs;
    }

    @Override
    protected PcmExperienceSimulationExecutor<PCMInstance, QVTOReconfigurator, QVToReconfiguration, Double> doCreate(
            Experiment experiment, DynamicBayesianNetwork<CategoricalValue> dbn) {
        DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess = new DeltaIoTModelAccess<>();
        SimulationRunnerHolder simulationRunnerHolder = createSimulationRunnerHolder();
        DeltaIoTPartiallyEnvDynamics<Double> p = new DeltaIoTPartiallyEnvDynamics<>(dbn, getSimulatedExperienceStore(),
                modelAccess, getSeedProvider(), simulationRunnerHolder);
        SelfAdaptiveSystemStateSpaceNavigator<PCMInstance, QVTOReconfigurator, Double, List<InputValue<CategoricalValue>>> envProcess = p
            .getEnvironmentProcess();

        String strategyId = getWorkflowConfiguration().getSimulationParameters()
            .getSimulationID();
        Path prismFolder = getPrismFolder(strategyId);
        try {
            Files.createDirectories(prismFolder);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        Set<PrismFileUpdater<QVTOReconfigurator, List<InputValue<CategoricalValue>>>> prismFileUpdaters = new LinkedHashSet<>();
        List<PrismSimulatedMeasurementSpec> prismSimulatedMeasurementSpec = createSpecs(experiment);
        SimulatedMeasurementSpecification packetLossSpec = findPrismMeasurementSpec(prismSimulatedMeasurementSpec,
                "PacketLoss.prism");
        PacketLossPrismFileUpdater<QVTOReconfigurator> packetLossUpdater = new PacketLossPrismFileUpdater<>(
                (PrismSimulatedMeasurementSpec) packetLossSpec, modelAccess);
        prismFileUpdaters.add(packetLossUpdater);
        SimulatedMeasurementSpecification energyConsumptionSpec = findPrismMeasurementSpec(
                prismSimulatedMeasurementSpec, "EnergyConsumption.prism");
        EnergyConsumptionPrismFileUpdater<QVTOReconfigurator> engergyConsumptionUpdater = new EnergyConsumptionPrismFileUpdater<>(
                (PrismSimulatedMeasurementSpec) energyConsumptionSpec);
        prismFileUpdaters.add(engergyConsumptionUpdater);
        PrismGenerator<QVTOReconfigurator, List<InputValue<CategoricalValue>>> prismGenerator = new PrismFileUpdateGenerator<>(
                prismFileUpdaters);

        DeltaIoTReconfigurationParamRepository reconfParamsRepo = new DeltaIoTReconfigurationParamsLoader()
            .load(DISTRIBUTION_FACTORS);
        IQVToReconfigurationManager qvtoReconfigurationManager = createQvtoReconfigurationManager(experiment,
                getWorkflowConfiguration());
        qvtoReconfigurationManager.addModelsToTransform(reconfParamsRepo.eResource());

        IExperimentProvider experimentProvider = createExperimentProvider(experiment);
        DeltaIoTPcmBasedPrismExperienceSimulationRunner<QVTOReconfigurator> runner = new DeltaIoTPcmBasedPrismExperienceSimulationRunner<>(
                prismGenerator, prismFolder, strategyId, reconfParamsRepo, experimentProvider);
        List<Initializable> beforeExecutionInitializables = new ArrayList<>();
        Initializable beforeExecutionInitializable = new GlobalPcmBeforeExecutionInitialization(experimentProvider,
                qvtoReconfigurationManager);
        beforeExecutionInitializables.add(beforeExecutionInitializable);

        // Strategy: DeltaIoTDefaultReconfigurationStrategy
        SystemConfigurationTracker systemConfigTracker = new SystemConfigurationTracker(getSimulationParameters());
        Path csvPath = getCSVPath(strategyId);
        try {
            Files.createDirectories(csvPath.getParent());
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        DeltaIotCSVSystemConfigurationStatisticSink csvSink = new DeltaIotCSVSystemConfigurationStatisticSink(csvPath);
        systemConfigTracker.addStatisticSink(csvSink);

        IDeltaIoToReconfCustomizerFactory reconfCustomizerFactory = new DeltaIoToReconfCustomizerFactory(
                reconfParamsRepo);
        DeltaIoToReconfCustomizerResolver reconfCustomizerResolver = new DeltaIoToReconfCustomizerResolver();

        Policy<QVTOReconfigurator, QVToReconfiguration> reconfSelectionPolicy = new DeltaIoTDefaultReconfigurationStrategy(
                reconfParamsRepo, modelAccess, getSimulationParameters(), systemConfigTracker,
                reconfCustomizerResolver);
        // Strategy: LocalQualityBasedReconfigurationStrategy
//        Policy<QVTOReconfigurator, QVToReconfiguration> reconfSelectionPolicy = LocalQualityBasedReconfigurationStrategy
//            .newBuilder(modelAccess)
//            .withReconfigurationParams(reconfParamsRepo)
//            .andPacketLossSpec((PrismSimulatedMeasurementSpec) packetLossSpec)
//            .andEnergyConsumptionSpec((PrismSimulatedMeasurementSpec) energyConsumptionSpec)
//            .build();

        RewardEvaluator<Double> evaluator = new QualityBasedRewardEvaluator(packetLossSpec, energyConsumptionSpec);

        IQVToReconfigurationProvider qvToReconfigurationProvider = qvtoReconfigurationManager
            .getQVToReconfigurationProvider();
        DeltaIoTQVToReconfigurationProvider deltaIoTQVTOReconfigurationProvider = new DeltaIoTQVToReconfigurationProvider(
                qvToReconfigurationProvider, reconfCustomizerFactory);
        Set<QVToReconfiguration> reconfigurations = deltaIoTQVTOReconfigurationProvider.getReconfigurations();

        DeltaIoTSampleLogger deltaIoTSampleLogger = new DeltaIoTSampleLogger(modelAccess);
        ExperienceSimulator<PCMInstance, QVTOReconfigurator, Double> simulator = createExperienceSimulator(experiment,
                prismSimulatedMeasurementSpec, List.of(runner), getSimulationParameters(),
                beforeExecutionInitializables, null, getSimulatedExperienceStore(), envProcess, reconfSelectionPolicy,
                reconfigurations, evaluator, false, experimentProvider, simulationRunnerHolder, deltaIoTSampleLogger,
                getSeedProvider());

        String sampleSpaceId = SimulatedExperienceConstants
            .constructSampleSpaceId(getSimulationParameters().getSimulationID(), reconfSelectionPolicy.getId());
//        TotalRewardCalculation rewardCalculation = SimulatedExperienceEvaluator
//            .of(getSimulationParameters().getSimulationID(), sampleSpaceId);
        TotalRewardCalculation rewardCalculation = new ExpectedRewardEvaluator(
                getSimulationParameters().getSimulationID(), sampleSpaceId);

        return new PcmExperienceSimulationExecutor<>(simulator, experiment, getSimulationParameters(),
                reconfSelectionPolicy, rewardCalculation, experimentProvider);
    }

    private Path getPrismFolder(String strategyId) {
        IPath workspaceBasePath = ResourcesPlugin.getWorkspace()
            .getRoot()
            .getLocation();
        Path outputBasePath = Paths.get(workspaceBasePath.toString());
        Path resourcePath = outputBasePath.resolve("resource");
        Path prismStrategyPath = resourcePath.resolve(strategyId);
        Path prismPath = prismStrategyPath.resolve("prism");
        return prismPath;
    }

    private Path getCSVPath(String strategyId) {
        IPath workspaceBasePath = ResourcesPlugin.getWorkspace()
            .getRoot()
            .getLocation();
        Path outputBasePath = Paths.get(workspaceBasePath.toString(), "resource", strategyId);

        String csvFileName = strategyId + "Configurations.csv";
        Path csvFilePath = Paths.get(outputBasePath.toString(), csvFileName);
        return csvFilePath;
    }

    private SimulatedMeasurementSpecification findPrismMeasurementSpec(List<PrismSimulatedMeasurementSpec> specs,
            String id) {
        for (SimulatedMeasurementSpecification simulatedMeasurementSpecification : specs) {
            if (0 == simulatedMeasurementSpecification.getId()
                .compareTo(id)) {
                return simulatedMeasurementSpecification;
            }
        }
        return null;
    }
}
