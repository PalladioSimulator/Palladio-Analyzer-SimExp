package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
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
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.action.SingleQVToReconfiguration;
import org.palladiosimulator.simexp.pcm.config.IPrismWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.process.DeltaIoTPcmBasedPrismExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.process.EnergyConsumptionPrismFileUpdater;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.process.PacketLossPrismFileUpdater;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.provider.PrismMeasurementSpecificationProvider;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.DeltaIoTNetworkReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.DistributionFactorReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.TransmissionPowerReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reward.QualityBasedRewardEvaluator;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.DeltaIoTDefaultReconfigurationStrategy;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTModelAccess;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTReconfigurationParamsLoader;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIotCSVSystemConfigurationStatisticSink;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIotConsoleSystemConfigurationStatisticSink;
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
import org.palladiosimulator.solver.models.PCMInstance;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public class DeltaIoTSimulationExecutorFactory extends
        PcmExperienceSimulationExecutorFactory<Double, List<InputValue<CategoricalValue>>, PrismSimulatedMeasurementSpec> {
    private static final Logger LOGGER = Logger.getLogger(DeltaIoTSimulationExecutorFactory.class);
    public final static String DELTAIOT_PATH = "/org.palladiosimulator.envdyn.examples.deltaiot";
    public final static String DISTRIBUTION_FACTORS = DELTAIOT_PATH
            + "/model/DeltaIoTReconfigurationParams.reconfigurationparams";

    public DeltaIoTSimulationExecutorFactory(IPrismWorkflowConfiguration workflowConfiguration,
            ModelLoader.Factory modelLoaderFactory,
            SimulatedExperienceStore<QVTOReconfigurator, Double> simulatedExperienceStore) {
        super(workflowConfiguration, modelLoaderFactory, simulatedExperienceStore);
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
                modelAccess, simulationRunnerHolder);
        SelfAdaptiveSystemStateSpaceNavigator<PCMInstance, QVTOReconfigurator, Double, List<InputValue<CategoricalValue>>> envProcess = p
            .getEnvironmentProcess();

        String strategyId = getWorkflowConfiguration().getSimulationParameters()
            .getSimulationID();
        Path prismLogFolder = getPrismLogFolder(strategyId);
        try {
            Files.createDirectories(prismLogFolder);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        Path prismLogPath = getPrismLogPath(strategyId);

        Set<PrismFileUpdater<QVTOReconfigurator, List<InputValue<CategoricalValue>>>> prismFileUpdaters = new HashSet<>();
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
        IQVToReconfigurationManager qvtoReconfigurationManager = createQvtoReconfigurationManager(experiment);
        qvtoReconfigurationManager.addModelsToTransform(reconfParamsRepo.eResource());

        IExperimentProvider experimentProvider = createExperimentProvider(experiment);
        DeltaIoTPcmBasedPrismExperienceSimulationRunner<QVTOReconfigurator> runner = new DeltaIoTPcmBasedPrismExperienceSimulationRunner<>(
                prismGenerator, prismLogPath, reconfParamsRepo, experimentProvider);
        FileDumperPrismObserver observer = new FileDumperPrismObserver(prismLogFolder);
        runner.addPrismObserver(observer);
        Initializable beforeExecutionInitializable = new GlobalPcmBeforeExecutionInitialization(experimentProvider,
                qvtoReconfigurationManager);

        // Strategy: DeltaIoTDefaultReconfigurationStrategy
        SystemConfigurationTracker systemConfigTracker = new SystemConfigurationTracker(getSimulationParameters());
        DeltaIotConsoleSystemConfigurationStatisticSink consoleSink = new DeltaIotConsoleSystemConfigurationStatisticSink();
        systemConfigTracker.addStatisticSink(consoleSink);
        Path csvPath = getCSVPath(strategyId);
        try {
            Files.createDirectories(csvPath.getParent());
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        DeltaIotCSVSystemConfigurationStatisticSink csvSink = new DeltaIotCSVSystemConfigurationStatisticSink(csvPath);
        systemConfigTracker.addStatisticSink(csvSink);

        Policy<QVTOReconfigurator, QVToReconfiguration> reconfSelectionPolicy = new DeltaIoTDefaultReconfigurationStrategy(
                reconfParamsRepo, modelAccess, getSimulationParameters(), systemConfigTracker);
        // Strategy: LocalQualityBasedReconfigurationStrategy
//        Policy<QVTOReconfigurator, QVToReconfiguration> reconfSelectionPolicy = LocalQualityBasedReconfigurationStrategy
//            .newBuilder(modelAccess)
//            .withReconfigurationParams(reconfParamsRepo)
//            .andPacketLossSpec((PrismSimulatedMeasurementSpec) packetLossSpec)
//            .andEnergyConsumptionSpec((PrismSimulatedMeasurementSpec) energyConsumptionSpec)
//            .build();

        RewardEvaluator<Double> evaluator = new QualityBasedRewardEvaluator(packetLossSpec, energyConsumptionSpec);

        Set<QVToReconfiguration> reconfigurations = buildreconfigurations(reconfParamsRepo, qvtoReconfigurationManager);

        ExperienceSimulator<PCMInstance, QVTOReconfigurator, Double> simulator = createExperienceSimulator(experiment,
                prismSimulatedMeasurementSpec, List.of(runner), getSimulationParameters(), beforeExecutionInitializable,
                null, getSimulatedExperienceStore(), envProcess, reconfSelectionPolicy, reconfigurations, evaluator,
                false, experimentProvider, simulationRunnerHolder);

        String sampleSpaceId = SimulatedExperienceConstants
            .constructSampleSpaceId(getSimulationParameters().getSimulationID(), reconfSelectionPolicy.getId());
//        TotalRewardCalculation rewardCalculation = SimulatedExperienceEvaluator
//            .of(getSimulationParameters().getSimulationID(), sampleSpaceId);
        TotalRewardCalculation rewardCalculation = new ExpectedRewardEvaluator(
                getSimulationParameters().getSimulationID(), sampleSpaceId);

        return new PcmExperienceSimulationExecutor<>(simulator, experiment, getSimulationParameters(),
                reconfSelectionPolicy, rewardCalculation, experimentProvider, qvtoReconfigurationManager);
    }

    private Set<QVToReconfiguration> buildreconfigurations(DeltaIoTReconfigurationParamRepository reconfParamsRepo,
            IQVToReconfigurationManager qvtoReconfigurationManager) {
        Set<QVToReconfiguration> reconfigurations = new HashSet<>();
        List<QVToReconfiguration> reconfigurationList = qvtoReconfigurationManager.loadReconfigurations();
        for (QVToReconfiguration qvto : reconfigurationList) {
            if (DistributionFactorReconfiguration.isCorrectQvtReconfguration(qvto)) {
                SingleQVToReconfiguration singleQVToReconfiguration = (SingleQVToReconfiguration) qvto;
                reconfigurations.add(new DistributionFactorReconfiguration(singleQVToReconfiguration,
                        reconfParamsRepo.getDistributionFactors()));
            } else if (TransmissionPowerReconfiguration.isCorrectQvtReconfguration(qvto)) {
                SingleQVToReconfiguration singleQVToReconfiguration = (SingleQVToReconfiguration) qvto;
                reconfigurations.add(new TransmissionPowerReconfiguration(singleQVToReconfiguration,
                        reconfParamsRepo.getTransmissionPower()));
            } else if (DeltaIoTNetworkReconfiguration.isCorrectQvtReconfguration(qvto)) {
                SingleQVToReconfiguration singleQVToReconfiguration = (SingleQVToReconfiguration) qvto;
                reconfigurations.add(new DeltaIoTNetworkReconfiguration(singleQVToReconfiguration, reconfParamsRepo));
            }
        }
        return reconfigurations;
    }

    private Path getPrismLogFolder(String strategyId) {
        IPath workspaceBasePath = ResourcesPlugin.getWorkspace()
            .getRoot()
            .getLocation();
        Path outputBasePath = Paths.get(workspaceBasePath.toString());
        Path resourcePath = outputBasePath.resolve("resource");
        Path prismPath = resourcePath.resolve("prism");
        Path prismStrategyPath = prismPath.resolve(strategyId);
        return prismStrategyPath;
    }

    private Path getPrismLogPath(String strategyId) {
        Path basePath = getPrismLogFolder(strategyId);
        Path prismLogPath = Paths.get(basePath.toString(), "prism.log");
        return prismLogPath;
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
