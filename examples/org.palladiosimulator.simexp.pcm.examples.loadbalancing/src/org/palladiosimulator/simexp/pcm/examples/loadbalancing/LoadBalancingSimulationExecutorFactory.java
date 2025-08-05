package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.evaluation.TotalRewardCalculation;
import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.reward.ThresholdBasedRewardEvaluator;
import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator;
import org.palladiosimulator.simexp.core.state.SimulationRunnerHolder;
import org.palladiosimulator.simexp.core.store.ISimulatedExperienceAccessor;
import org.palladiosimulator.simexp.core.store.ISimulatedExperienceStore;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationProvider;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.init.GlobalPcmBeforeExecutionInitialization;
import org.palladiosimulator.simexp.pcm.process.PcmExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.simulator.SimulatorPcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.simulator.config.IPCMWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.state.InitialPcmStateCreator;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.core.models.PCMInstance;

import tools.mdsd.probdist.api.entity.CategoricalValue;
import tools.mdsd.probdist.api.random.ISeedProvider;

public class LoadBalancingSimulationExecutorFactory
        extends SimulatorPcmExperienceSimulationExecutorFactory<Integer, List<InputValue<CategoricalValue>>> {
    public final static double UPPER_THRESHOLD_RT = 2.0;
    public final static double LOWER_THRESHOLD_RT = 0.3;

    public LoadBalancingSimulationExecutorFactory(IPCMWorkflowConfiguration workflowConfiguration,
            ModelLoader.Factory modelLoaderFactory,
            ISimulatedExperienceStore<QVTOReconfigurator, Integer> simulatedExperienceStore,
            Optional<ISeedProvider> seedProvider, ISimulatedExperienceAccessor accessor, Path resourcePath) {
        super(workflowConfiguration, modelLoaderFactory, simulatedExperienceStore, seedProvider, accessor,
                resourcePath);
    }

    @Override
    protected PcmExperienceSimulationExecutor<PCMInstance, QVTOReconfigurator, QVToReconfiguration, Integer> doCreate(
            Experiment experiment, DynamicBayesianNetwork<CategoricalValue> dbn) {
        IExperimentProvider experimentProvider = createExperimentProvider(experiment);
        VaryingInterarrivelRateProcess<QVTOReconfigurator, QVToReconfiguration, Integer> p = new VaryingInterarrivelRateProcess<>(
                dbn, experimentProvider);
        EnvironmentProcess<QVTOReconfigurator, Integer, List<InputValue<CategoricalValue>>> envProcess = p
            .getEnvironmentProcess();
        p.init(getSeedProvider());

        List<PcmMeasurementSpecification> pcmMeasurementSpecs = createSpecs(experiment);
        Set<SimulatedMeasurementSpecification> simulatedMeasurementSpecs = new LinkedHashSet<>(pcmMeasurementSpecs);
        SimulationRunnerHolder simulationRunnerHolder = createSimulationRunnerHolder();
        InitialPcmStateCreator<QVTOReconfigurator, List<InputValue<CategoricalValue>>> initialStateCreator = new InitialPcmStateCreator<>(
                simulatedMeasurementSpecs, experimentProvider, simulationRunnerHolder);

        List<ExperienceSimulationRunner> simulationRunners = List
            .of(new PcmExperienceSimulationRunner<>(experimentProvider, initialStateCreator));
        IQVToReconfigurationManager qvtoReconfigurationManager = createQvtoReconfigurationManager(experiment,
                getWorkflowConfiguration());
        List<Initializable> beforeExecutionInitializables = new ArrayList<>();
        Initializable beforeExecutionInitializable = new GlobalPcmBeforeExecutionInitialization(experimentProvider,
                qvtoReconfigurationManager);
        beforeExecutionInitializables.add(beforeExecutionInitializable);
        Policy<QVTOReconfigurator, QVToReconfiguration> reconfSelectionPolicy = new NStepLoadBalancerStrategy<PCMInstance, QVTOReconfigurator>(
                1, pcmMeasurementSpecs.get(0), UPPER_THRESHOLD_RT, LOWER_THRESHOLD_RT);

        Pair<SimulatedMeasurementSpecification, Threshold> threshold = Pair.of(pcmMeasurementSpecs.get(0),
                Threshold.lessThanOrEqualTo(UPPER_THRESHOLD_RT));
        RewardEvaluator<Integer> evaluator = ThresholdBasedRewardEvaluator.with(threshold);

        IQVToReconfigurationProvider qvToReconfigurationProvider = qvtoReconfigurationManager
            .getQVToReconfigurationProvider();
        Set<QVToReconfiguration> reconfigurations = qvToReconfigurationProvider.getReconfigurations();

        ExperienceSimulator<PCMInstance, QVTOReconfigurator, Integer> simulator = createExperienceSimulator(experiment,
                pcmMeasurementSpecs, simulationRunners, getSimulationParameters(), beforeExecutionInitializables,
                envProcess, getSimulatedExperienceStore(), null, reconfSelectionPolicy, reconfigurations, evaluator,
                false, experimentProvider, simulationRunnerHolder, null, getSeedProvider());

        TotalRewardCalculation rewardCalculation = createRewardCalculation(reconfSelectionPolicy.getId());
        IQualityEvaluator qualityEvaluator = createQualityEvaluator(pcmMeasurementSpecs);

        return new PcmExperienceSimulationExecutor<>(simulator, experiment, getSimulationParameters(),
                reconfSelectionPolicy, rewardCalculation, qualityEvaluator, experimentProvider);
    }
}
