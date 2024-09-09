package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
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
import org.palladiosimulator.solver.models.PCMInstance;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public class LoadBalancingSimulationExecutorFactory
        extends SimulatorPcmExperienceSimulationExecutorFactory<Integer, List<InputValue<CategoricalValue>>> {
    public final static double UPPER_THRESHOLD_RT = 2.0;
    public final static double LOWER_THRESHOLD_RT = 0.3;

    public LoadBalancingSimulationExecutorFactory(IPCMWorkflowConfiguration workflowConfiguration,
            ModelLoader.Factory modelLoaderFactory,
            SimulatedExperienceStore<QVTOReconfigurator, Integer> simulatedExperienceStore) {
        super(workflowConfiguration, modelLoaderFactory, simulatedExperienceStore);
    }

    @Override
    protected PcmExperienceSimulationExecutor<PCMInstance, QVTOReconfigurator, QVToReconfiguration, Integer> doCreate(
            Experiment experiment, DynamicBayesianNetwork<CategoricalValue> dbn) {
        IExperimentProvider experimentProvider = createExperimentProvider(experiment);
        VaryingInterarrivelRateProcess<QVTOReconfigurator, QVToReconfiguration, Integer> p = new VaryingInterarrivelRateProcess<>(
                dbn, experimentProvider);
        EnvironmentProcess<QVTOReconfigurator, Integer, List<InputValue<CategoricalValue>>> envProcess = p
            .getEnvironmentProcess();

        List<PcmMeasurementSpecification> pcmMeasurementSpecs = createSpecs(experiment);
        Set<SimulatedMeasurementSpecification> simulatedMeasurementSpecs = new HashSet<>(pcmMeasurementSpecs);
        SimulationRunnerHolder simulationRunnerHolder = createSimulationRunnerHolder();
        InitialPcmStateCreator<QVTOReconfigurator, List<InputValue<CategoricalValue>>> initialStateCreator = new InitialPcmStateCreator<>(
                simulatedMeasurementSpecs, experimentProvider, simulationRunnerHolder);

        List<ExperienceSimulationRunner> simulationRunners = List
            .of(new PcmExperienceSimulationRunner<>(experimentProvider, initialStateCreator));
        IQVToReconfigurationManager qvtoReconfigurationManager = createQvtoReconfigurationManager(experiment,
                getWorkflowConfiguration());
        Initializable beforeExecutionInitializable = new GlobalPcmBeforeExecutionInitialization(experimentProvider,
                qvtoReconfigurationManager);
        Policy<QVTOReconfigurator, QVToReconfiguration> reconfSelectionPolicy = new NStepLoadBalancerStrategy<PCMInstance, QVTOReconfigurator>(
                1, pcmMeasurementSpecs.get(0), UPPER_THRESHOLD_RT, LOWER_THRESHOLD_RT);

        Pair<SimulatedMeasurementSpecification, Threshold> threshold = Pair.of(pcmMeasurementSpecs.get(0),
                Threshold.lessThanOrEqualTo(UPPER_THRESHOLD_RT));
        RewardEvaluator<Integer> evaluator = ThresholdBasedRewardEvaluator.with(threshold);

        IQVToReconfigurationProvider qvToReconfigurationProvider = qvtoReconfigurationManager
            .getQVToReconfigurationProvider();
        Set<QVToReconfiguration> reconfigurations = qvToReconfigurationProvider.getReconfigurations();

        ExperienceSimulator<PCMInstance, QVTOReconfigurator, Integer> simulator = createExperienceSimulator(experiment,
                pcmMeasurementSpecs, simulationRunners, getSimulationParameters(), beforeExecutionInitializable,
                envProcess, getSimulatedExperienceStore(), null, reconfSelectionPolicy, reconfigurations, evaluator,
                false, experimentProvider, simulationRunnerHolder);

        String sampleSpaceId = SimulatedExperienceConstants
            .constructSampleSpaceId(getSimulationParameters().getSimulationID(), reconfSelectionPolicy.getId());
        TotalRewardCalculation rewardCalculation = SimulatedExperienceEvaluator
            .of(getSimulationParameters().getSimulationID(), sampleSpaceId);

        return new PcmExperienceSimulationExecutor<>(simulator, experiment, getSimulationParameters(),
                reconfSelectionPolicy, rewardCalculation, experimentProvider);
    }
}
