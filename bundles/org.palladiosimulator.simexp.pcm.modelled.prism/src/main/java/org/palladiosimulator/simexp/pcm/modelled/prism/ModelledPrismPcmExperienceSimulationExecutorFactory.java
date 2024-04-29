package org.palladiosimulator.simexp.pcm.modelled.prism;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.evaluation.SimulatedExperienceEvaluator;
import org.palladiosimulator.simexp.core.evaluation.TotalRewardCalculation;
import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.reward.ThresholdBasedRewardEvaluator;
import org.palladiosimulator.simexp.core.state.SimulationRunnerHolder;
import org.palladiosimulator.simexp.core.statespace.SelfAdaptiveSystemStateSpaceNavigator;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.SmodelInterpreter;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mape.Monitor;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.mape.PcmMonitor;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.value.IModelsLookup;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.value.ModelsLookup;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.value.PcmProbeValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.value.EnvironmentVariableValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.model.strategy.ModelledReconfigurationStrategy;
import org.palladiosimulator.simexp.model.strategy.ModelledSimulationExecutor;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.DeltaIoTModelAccess;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.DeltaIoTPartiallyEnvDynamics;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.DeltaIoTPcmBasedPrismExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.DeltaIoTReconfigurationParamsLoader;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.DistributionFactorReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.EnergyConsumptionPrismFileUpdater;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.GlobalQualityBasedReconfigurationStrategy;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.PacketLossPrismFileUpdater;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.TransmissionPowerReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.init.GlobalPcmBeforeExecutionInitialization;
import org.palladiosimulator.simexp.pcm.modelled.prism.config.IModelledPrismWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.prism.generator.PrismFileUpdateGenerator;
import org.palladiosimulator.simexp.pcm.prism.generator.PrismFileUpdateGenerator.PrismFileUpdater;
import org.palladiosimulator.simexp.pcm.prism.generator.PrismGenerator;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public class ModelledPrismPcmExperienceSimulationExecutorFactory
        extends ModelledPrismExperienceSimulationExecutorFactory<Integer, List<InputValue<CategoricalValue>>> {

    public final static String DELTAIOT_PATH = "/org.palladiosimulator.envdyn.examples.deltaiot";
    public final static String DISTRIBUTION_FACTORS = DELTAIOT_PATH
            + "/model/DeltaIoTReconfigurationParams.reconfigurationparams";
    public final static String PRISM_FOLDER = "prism";

    public ModelledPrismPcmExperienceSimulationExecutorFactory(
            IModelledPrismWorkflowConfiguration workflowConfiguration, ResourceSet rs,
            SimulatedExperienceStore<QVTOReconfigurator, Integer> simulatedExperienceStore) {
        super(workflowConfiguration, rs, simulatedExperienceStore);
    }

    @Override
    protected PcmExperienceSimulationExecutor<PCMInstance, QVTOReconfigurator, QVToReconfiguration, Integer> doCreate() {
        DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess = new DeltaIoTModelAccess<>();
        SimulationRunnerHolder simulationRunnerHolder = createSimulationRunnerHolder();
        DeltaIoTPartiallyEnvDynamics<Integer> p = new DeltaIoTPartiallyEnvDynamics<>(getDbn(),
                getSimulatedExperienceStore(), modelAccess, simulationRunnerHolder);
        SelfAdaptiveSystemStateSpaceNavigator<PCMInstance, QVTOReconfigurator, Integer, List<InputValue<CategoricalValue>>> envProcess = p
            .getEnvironmentProcess();

        Set<PrismFileUpdater<QVTOReconfigurator, List<InputValue<CategoricalValue>>>> prismFileUpdaters = new HashSet<>();
        Experiment experiment = loadExperiment();
        List<PrismSimulatedMeasurementSpec> prismSimulatedMeasurementSpec = createSpecs(experiment);
        SimulatedMeasurementSpecification packetLossSpec = findPrismMeasurementSpec(prismSimulatedMeasurementSpec,
                "PacketLoss.prism");
        PacketLossPrismFileUpdater<QVTOReconfigurator> packetLossUpdater = new PacketLossPrismFileUpdater<>(
                (PrismSimulatedMeasurementSpec) packetLossSpec);
        prismFileUpdaters.add(packetLossUpdater);
        SimulatedMeasurementSpecification energyConsumptionSpec = findPrismMeasurementSpec(
                prismSimulatedMeasurementSpec, "EnergyConsumption.prism");
        EnergyConsumptionPrismFileUpdater<QVTOReconfigurator> engergyConsumptionUpdater = new EnergyConsumptionPrismFileUpdater<>(
                (PrismSimulatedMeasurementSpec) energyConsumptionSpec);
        prismFileUpdaters.add(engergyConsumptionUpdater);

        PrismGenerator<QVTOReconfigurator, List<InputValue<CategoricalValue>>> prismGenerator = new PrismFileUpdateGenerator<>(
                prismFileUpdaters);

        URI uri = URI.createPlatformResourceURI(Paths.get(DELTAIOT_PATH, PRISM_FOLDER)
            .toString(), true);
        File prismLogFile = new File(CommonPlugin.resolve(uri)
            .toFileString());

        DeltaIoTReconfigurationParamRepository reconfParamsRepo = new DeltaIoTReconfigurationParamsLoader()
            .load(DISTRIBUTION_FACTORS);
        IExperimentProvider experimentProvider = createExperimentProvider(experiment);
        ExperienceSimulationRunner runner = new DeltaIoTPcmBasedPrismExperienceSimulationRunner<>(prismGenerator,
                prismLogFile, reconfParamsRepo, experimentProvider);

        IQVToReconfigurationManager qvtoReconfigurationManager = createQvtoReconfigurationManager(experiment);
        qvtoReconfigurationManager.addModelsToTransform(reconfParamsRepo.eResource());
        Initializable beforeExecutionInitializable = new GlobalPcmBeforeExecutionInitialization(experimentProvider,
                qvtoReconfigurationManager);

        List<SimulatedMeasurementSpecification> simSpecs = new ArrayList<>(prismSimulatedMeasurementSpec);
        IModelsLookup modelsLookup = new ModelsLookup(experiment);
        PcmProbeValueProvider probeValueProvider = new PcmProbeValueProvider(modelsLookup);
        EnvironmentVariableValueProvider environmentVariableValueProvider = new EnvironmentVariableValueProvider(
                probabilisticModelRepository);
        Monitor monitor = new PcmMonitor(simSpecs, probeValueProvider, environmentVariableValueProvider);
        Smodel smodel = getSmodel();
        SmodelInterpreter smodelInterpreter = new SmodelInterpreter(smodel, probeValueProvider,
                environmentVariableValueProvider);
        String reconfigurationStrategyId = smodel.getModelName();
//        Policy<QVTOReconfigurator, QVToReconfiguration> reconfStrategy = LocalQualityBasedReconfigurationStrategy
//            .newBuilder(modelAccess)
//            .withReconfigurationParams(reconfParamsRepo)
//            .andPacketLossSpec((PrismSimulatedMeasurementSpec) packetLossSpec)
//            .andEnergyConsumptionSpec((PrismSimulatedMeasurementSpec) energyConsumptionSpec)
//            .build();
        Policy<QVTOReconfigurator, QVToReconfiguration> reconfStrategy = new ModelledReconfigurationStrategy(
                reconfigurationStrategyId, monitor, smodelInterpreter, smodelInterpreter, qvtoReconfigurationManager);

        Set<QVToReconfiguration> reconfigurations = new HashSet<>();
        List<QVToReconfiguration> reconfigurationList = qvtoReconfigurationManager.loadReconfigurations();
        reconfigurationList.stream()
            .forEach(qvto -> {
                if (DistributionFactorReconfiguration.isCorrectQvtReconfguration(qvto)) {
                    reconfigurations
                        .add(new DistributionFactorReconfiguration(qvto, reconfParamsRepo.getDistributionFactors()));
                } else if (TransmissionPowerReconfiguration.isCorrectQvtReconfguration(qvto)) {
                    reconfigurations
                        .add(new TransmissionPowerReconfiguration(qvto, reconfParamsRepo.getTransmissionPower()));
                }
            });

        // FIXME: read thresholds from launch config
        Pair<SimulatedMeasurementSpecification, Threshold> lowerPacketLossThreshold = Pair
            .of(prismSimulatedMeasurementSpec.get(0), GlobalQualityBasedReconfigurationStrategy.LOWER_PACKET_LOSS);
        Pair<SimulatedMeasurementSpecification, Threshold> lowerEnergyConsumptionThreshold = Pair.of(
                prismSimulatedMeasurementSpec.get(1),
                GlobalQualityBasedReconfigurationStrategy.LOWER_ENERGY_CONSUMPTION);
        RewardEvaluator<Integer> evaluator = ThresholdBasedRewardEvaluator.with(lowerPacketLossThreshold,
                lowerEnergyConsumptionThreshold);

        ExperienceSimulator<PCMInstance, QVTOReconfigurator, Integer> experienceSimulator = createExperienceSimulator(
                experiment, prismSimulatedMeasurementSpec, List.of(runner), getSimulationParameters(),
                beforeExecutionInitializable, null, getSimulatedExperienceStore(), envProcess, reconfStrategy,
                reconfigurations, evaluator, false, experimentProvider, simulationRunnerHolder);

        String sampleSpaceId = SimulatedExperienceConstants
            .constructSampleSpaceId(getSimulationParameters().getSimulationID(), reconfigurationStrategyId);
        TotalRewardCalculation rewardCalculation = SimulatedExperienceEvaluator
            .of(getSimulationParameters().getSimulationID(), sampleSpaceId);
        ModelledSimulationExecutor<Integer> executor = new ModelledSimulationExecutor<>(experienceSimulator, experiment,
                getSimulationParameters(), reconfStrategy, rewardCalculation, experimentProvider,
                qvtoReconfigurationManager);
        return executor;
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
