package org.palladiosimulator.simexp.pcm.modelled.prism;

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
import org.palladiosimulator.envdyn.environment.staticmodel.ProbabilisticModelRepository;
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
import org.palladiosimulator.simexp.dsl.smodel.interpreter.SmodelInterpreter;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mape.Monitor;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.mape.PcmMonitor;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.value.IModelsLookup;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.value.ModelsLookup;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.value.PcmProbeValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.value.EnvironmentVariableValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.sampling.SampleDumper;
import org.palladiosimulator.simexp.model.strategy.ModelledReconfigurationStrategy;
import org.palladiosimulator.simexp.model.strategy.ModelledSimulationExecutor;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationProvider;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.DeltaIoTPartiallyEnvDynamics;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.DeltaIoTSampleLogger;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.FileDumperPrismObserver;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.process.DeltaIoTPcmBasedPrismExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.process.EnergyConsumptionPrismFileUpdater;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.process.PacketLossPrismFileUpdater;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reward.QualityBasedRewardEvaluator;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTModelAccess;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTReconfigurationParamsLoader;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.init.GlobalPcmBeforeExecutionInitialization;
import org.palladiosimulator.simexp.pcm.modelled.ModelledModelLoader;
import org.palladiosimulator.simexp.pcm.modelled.prism.config.IModelledPrismWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.prism.generator.PrismFileUpdateGenerator;
import org.palladiosimulator.simexp.pcm.prism.generator.PrismFileUpdateGenerator.PrismFileUpdater;
import org.palladiosimulator.simexp.pcm.prism.generator.PrismGenerator;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

import tools.mdsd.probdist.api.entity.CategoricalValue;
import tools.mdsd.probdist.api.random.ISeedProvider;

public class ModelledPrismPcmExperienceSimulationExecutorFactory
        extends ModelledPrismExperienceSimulationExecutorFactory<Double, List<InputValue<CategoricalValue>>> {
    private static final Logger LOGGER = Logger.getLogger(ModelledPrismPcmExperienceSimulationExecutorFactory.class);
    public final static String DELTAIOT_PATH = "/org.palladiosimulator.envdyn.examples.deltaiot";
    public final static String DISTRIBUTION_FACTORS = DELTAIOT_PATH
            + "/model/DeltaIoTReconfigurationParams.reconfigurationparams";

    public ModelledPrismPcmExperienceSimulationExecutorFactory(
            IModelledPrismWorkflowConfiguration workflowConfiguration, ModelledModelLoader.Factory modelLoaderFactory,
            SimulatedExperienceStore<QVTOReconfigurator, Double> simulatedExperienceStore,
            Optional<ISeedProvider> seedProvider) {
        super(workflowConfiguration, modelLoaderFactory, simulatedExperienceStore, seedProvider);
    }

    @Override
    protected PcmExperienceSimulationExecutor<PCMInstance, QVTOReconfigurator, QVToReconfiguration, Double> doModelledCreate(
            Experiment experiment, ProbabilisticModelRepository probabilisticModelRepository,
            DynamicBayesianNetwork<CategoricalValue> dbn, Smodel smodel) {
        DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess = new DeltaIoTModelAccess<>();
        SimulationRunnerHolder simulationRunnerHolder = createSimulationRunnerHolder();
        DeltaIoTPartiallyEnvDynamics<Double> p = new DeltaIoTPartiallyEnvDynamics<>(dbn, getSimulatedExperienceStore(),
                modelAccess, getSeedProvider(), simulationRunnerHolder);
        SelfAdaptiveSystemStateSpaceNavigator<PCMInstance, QVTOReconfigurator, Double, List<InputValue<CategoricalValue>>> envProcess = p
            .getEnvironmentProcess();

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

        String reconfigurationStrategyId = smodel.getModelName();
        Path prismLogFolder = getPrismLogFolder(reconfigurationStrategyId);
        try {
            Files.createDirectories(prismLogFolder);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        Path prismLogPath = getPrismLogPath(reconfigurationStrategyId);

        DeltaIoTReconfigurationParamRepository reconfParamsRepo = new DeltaIoTReconfigurationParamsLoader()
            .load(DISTRIBUTION_FACTORS);
        IExperimentProvider experimentProvider = createExperimentProvider(experiment);
        DeltaIoTPcmBasedPrismExperienceSimulationRunner<QVTOReconfigurator> runner = new DeltaIoTPcmBasedPrismExperienceSimulationRunner<>(
                prismGenerator, prismLogPath, reconfParamsRepo, experimentProvider);
        FileDumperPrismObserver observer = new FileDumperPrismObserver(prismLogFolder);
        runner.addPrismObserver(observer);

        IQVToReconfigurationManager qvtoReconfigurationManager = createQvtoReconfigurationManager(experiment,
                getWorkflowConfiguration());
        qvtoReconfigurationManager.addModelsToTransform(reconfParamsRepo.eResource());
        List<Initializable> beforeExecutionInitializables = new ArrayList<>();
        Initializable globalPcmBeforeExecutionInitialization = new GlobalPcmBeforeExecutionInitialization(
                experimentProvider, qvtoReconfigurationManager);
        beforeExecutionInitializables.add(globalPcmBeforeExecutionInitialization);

        List<SimulatedMeasurementSpecification> simSpecs = new ArrayList<>(prismSimulatedMeasurementSpec);
        IModelsLookup modelsLookup = new ModelsLookup(experiment);
        PcmProbeValueProvider probeValueProvider = new PcmProbeValueProvider(modelsLookup);
        EnvironmentVariableValueProvider environmentVariableValueProvider = new EnvironmentVariableValueProvider(
                probabilisticModelRepository);
        Monitor monitor = new PcmMonitor(simSpecs, probeValueProvider, environmentVariableValueProvider);
        SmodelInterpreter smodelInterpreter = new SmodelInterpreter(smodel, probeValueProvider,
                environmentVariableValueProvider);
        beforeExecutionInitializables.add(() -> smodelInterpreter.reset());
        SampleDumper sampleDumper = new DeltaIoTSampleLogger(modelAccess);
        Policy<QVTOReconfigurator, QVToReconfiguration> reconfStrategy = new ModelledReconfigurationStrategy(
                sampleDumper, reconfigurationStrategyId, monitor, smodelInterpreter, smodelInterpreter,
                qvtoReconfigurationManager);

        IQVToReconfigurationProvider qvToReconfigurationProvider = qvtoReconfigurationManager
            .getQVToReconfigurationProvider();
        PrismQVToReconfigurationProvider prismQVToReconfigurationProvider = new PrismQVToReconfigurationProvider(
                qvToReconfigurationProvider, reconfParamsRepo);
        Set<QVToReconfiguration> reconfigurations = prismQVToReconfigurationProvider.getReconfigurations();

        RewardEvaluator<Double> evaluator = new QualityBasedRewardEvaluator(packetLossSpec, energyConsumptionSpec);

        DeltaIoTSampleLogger deltaIoTSampleLogger = new DeltaIoTSampleLogger(modelAccess);
        ExperienceSimulator<PCMInstance, QVTOReconfigurator, Double> experienceSimulator = createExperienceSimulator(
                experiment, prismSimulatedMeasurementSpec, List.of(runner), getSimulationParameters(),
                beforeExecutionInitializables, null, getSimulatedExperienceStore(), envProcess, reconfStrategy,
                reconfigurations, evaluator, false, experimentProvider, simulationRunnerHolder, deltaIoTSampleLogger,
                getSeedProvider());

        String sampleSpaceId = SimulatedExperienceConstants
            .constructSampleSpaceId(getSimulationParameters().getSimulationID(), reconfigurationStrategyId);
//        TotalRewardCalculation rewardCalculation = SimulatedExperienceEvaluator
//            .of(getSimulationParameters().getSimulationID(), sampleSpaceId);
        TotalRewardCalculation rewardCalculation = new ExpectedRewardEvaluator(
                getSimulationParameters().getSimulationID(), sampleSpaceId);
        ModelledSimulationExecutor<Double> executor = new ModelledSimulationExecutor<>(experienceSimulator, experiment,
                getSimulationParameters(), reconfStrategy, rewardCalculation, experimentProvider);
        return executor;
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
