package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
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
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.config.SimulationParameters;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
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

import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.entity.CategoricalValue;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;
import tools.mdsd.probdist.api.parser.ParameterParser;

public class DeltaIoTSimulationExecutorFactory extends
        PcmExperienceSimulationExecutorFactory<Integer, List<InputValue<CategoricalValue>>, PrismSimulatedMeasurementSpec> {
    public final static String DELTAIOT_PATH = "/org.palladiosimulator.envdyn.examples.deltaiot";
    public final static String DISTRIBUTION_FACTORS = DELTAIOT_PATH
            + "/model/DeltaIoTReconfigurationParams.reconfigurationparams";
    public final static String PRISM_FOLDER = "prism";

    private final DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess;
    private final SelfAdaptiveSystemStateSpaceNavigator<PCMInstance, QVTOReconfigurator, Integer, List<InputValue<CategoricalValue>>> envProcess;
    private final IQVToReconfigurationManager qvtoReconfigurationManager;

    public DeltaIoTSimulationExecutorFactory(Experiment experiment, DynamicBayesianNetwork<CategoricalValue> dbn,
            List<PrismSimulatedMeasurementSpec> specs, SimulationParameters params,
            SimulatedExperienceStore<QVTOReconfigurator, Integer> simulatedExperienceStore,
            IProbabilityDistributionFactory<CategoricalValue> distributionFactory,
            IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry,
            ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup,
            IExperimentProvider experimentProvider, IQVToReconfigurationManager qvtoReconfigurationManager,
            SimulationRunnerHolder simulationRunnerHolder) {
        super(experiment, dbn, specs, params, simulatedExperienceStore, distributionFactory,
                probabilityDistributionRegistry, parameterParser, probDistRepoLookup, experimentProvider,
                simulationRunnerHolder);
        this.modelAccess = new DeltaIoTModelAccess<>();
        DeltaIoTPartiallyEnvDynamics<Integer> p = new DeltaIoTPartiallyEnvDynamics<>(dbn, simulatedExperienceStore,
                modelAccess, simulationRunnerHolder);
        this.envProcess = p.getEnvironmentProcess();
        this.qvtoReconfigurationManager = qvtoReconfigurationManager;
    }

    @Override
    public PcmExperienceSimulationExecutor<PCMInstance, QVTOReconfigurator, QVToReconfiguration, Integer> create() {
        URI uri = URI.createPlatformResourceURI(Paths.get(DELTAIOT_PATH, PRISM_FOLDER)
            .toString(), true);
        File prismLogFile = new File(CommonPlugin.resolve(uri)
            .toFileString());

        Set<PrismFileUpdater<QVTOReconfigurator, List<InputValue<CategoricalValue>>>> prismFileUpdaters = new HashSet<>();
        SimulatedMeasurementSpecification packetLossSpec = findPrismMeasurementSpec(getSpecs(), "PacketLoss.prism");
        PacketLossPrismFileUpdater<QVTOReconfigurator> packetLossUpdater = new PacketLossPrismFileUpdater<>(
                (PrismSimulatedMeasurementSpec) packetLossSpec);
        prismFileUpdaters.add(packetLossUpdater);
        SimulatedMeasurementSpecification energyConsumptionSpec = findPrismMeasurementSpec(getSpecs(),
                "EnergyConsumption.prism");
        EnergyConsumptionPrismFileUpdater<QVTOReconfigurator> engergyConsumptionUpdater = new EnergyConsumptionPrismFileUpdater<>(
                (PrismSimulatedMeasurementSpec) energyConsumptionSpec);
        prismFileUpdaters.add(engergyConsumptionUpdater);
        PrismGenerator<QVTOReconfigurator, List<InputValue<CategoricalValue>>> prismGenerator = new PrismFileUpdateGenerator<>(
                prismFileUpdaters);

        DeltaIoTReconfigurationParamRepository reconfParamsRepo = new DeltaIoTReconfigurationParamsLoader()
            .load(DISTRIBUTION_FACTORS);
        qvtoReconfigurationManager.addModelsToTransform(reconfParamsRepo.eResource());

        ExperienceSimulationRunner runner = new DeltaIoTPcmBasedPrismExperienceSimulationRunner<>(prismGenerator,
                prismLogFile, reconfParamsRepo, getExperimentProvider());
        Initializable beforeExecutionInitializable = new GlobalPcmBeforeExecutionInitialization(getExperimentProvider(),
                qvtoReconfigurationManager);

        Policy<QVTOReconfigurator, QVToReconfiguration> reconfSelectionPolicy = LocalQualityBasedReconfigurationStrategy
            .newBuilder(modelAccess)
            .withReconfigurationParams(reconfParamsRepo)
            .andPacketLossSpec((PrismSimulatedMeasurementSpec) packetLossSpec)
            .andEnergyConsumptionSpec((PrismSimulatedMeasurementSpec) energyConsumptionSpec)
            .build();

        Pair<SimulatedMeasurementSpecification, Threshold> lowerPacketLossThreshold = Pair.of(getSpecs().get(0),
                GlobalQualityBasedReconfigurationStrategy.LOWER_PACKET_LOSS);
        Pair<SimulatedMeasurementSpecification, Threshold> lowerEnergyConsumptionThreshold = Pair.of(getSpecs().get(1),
                GlobalQualityBasedReconfigurationStrategy.LOWER_ENERGY_CONSUMPTION);
        RewardEvaluator<Integer> evaluator = ThresholdBasedRewardEvaluator.with(lowerPacketLossThreshold,
                lowerEnergyConsumptionThreshold);

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

        ExperienceSimulator<PCMInstance, QVTOReconfigurator, Integer> simulator = createExperienceSimulator(getExperiment(),
                getSpecs(), List.of(runner), getSimulationParameters(), beforeExecutionInitializable, null, getSimulatedExperienceStore(),
                envProcess, reconfSelectionPolicy, reconfigurations, evaluator, false);

        String sampleSpaceId = SimulatedExperienceConstants.constructSampleSpaceId(getSimulationParameters().getSimulationID(),
                reconfSelectionPolicy.getId());
        TotalRewardCalculation rewardCalculation = SimulatedExperienceEvaluator.of(getSimulationParameters().getSimulationID(),
                sampleSpaceId);

        return new PcmExperienceSimulationExecutor<>(simulator, getExperiment(), getSimulationParameters(), reconfSelectionPolicy,
                rewardCalculation, getExperimentProvider(), qvtoReconfigurationManager);
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
