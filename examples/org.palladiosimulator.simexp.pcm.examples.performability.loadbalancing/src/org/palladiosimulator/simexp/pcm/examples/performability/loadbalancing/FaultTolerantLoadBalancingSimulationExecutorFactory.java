package org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.pcm.query.RepositoryModelLookup;
import org.palladiosimulator.pcm.query.ResourceEnvironmentModelLookup;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.evaluation.PerformabilityEvaluator;
import org.palladiosimulator.simexp.core.evaluation.TotalRewardCalculation;
import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.state.SimulationRunnerHolder;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.config.SimulationParameters;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.examples.performability.NodeRecoveryStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.PerformabilityStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.PerformabilityStrategyConfiguration;
import org.palladiosimulator.simexp.pcm.examples.performability.ReconfigurationPlanningStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.RepositoryModelUpdater;
import org.palladiosimulator.simexp.pcm.init.GlobalPcmBeforeExecutionInitialization;
import org.palladiosimulator.simexp.pcm.performability.PerformabilityRewardEvaluation;
import org.palladiosimulator.simexp.pcm.process.PerformabilityPcmExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.simulator.SimulatorPcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.simulator.config.IPCMWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.state.InitialPcmStateCreator;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.entity.CategoricalValue;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;
import tools.mdsd.probdist.api.parser.ParameterParser;

public class FaultTolerantLoadBalancingSimulationExecutorFactory
        extends SimulatorPcmExperienceSimulationExecutorFactory<Double, List<InputValue<CategoricalValue>>> {
    public static final Threshold LOWER_THRESHOLD_RT = Threshold.greaterThanOrEqualTo(0.1);
    public static final Threshold UPPER_THRESHOLD_RT = Threshold.lessThanOrEqualTo(0.4);

    public static final String SERVER_FAILURE_TEMPLATE_ID = "_VtIJEPtrEeuPUtFH1XJrHw";
    public static final String LOAD_BALANCER_ID = "_NvLi8AEmEeS7FKokKTKFow";

    public FaultTolerantLoadBalancingSimulationExecutorFactory(IPCMWorkflowConfiguration workflowConfiguration,
            ResourceSet rs, DynamicBayesianNetwork<CategoricalValue> dbn, SimulationParameters params,
            SimulatedExperienceStore<QVTOReconfigurator, Double> simulatedExperienceStore,
            IProbabilityDistributionFactory<CategoricalValue> distributionFactory,
            IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry,
            ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup) {
        super(workflowConfiguration, rs, dbn, params, simulatedExperienceStore, distributionFactory,
                probabilityDistributionRegistry, parameterParser, probDistRepoLookup);
    }

    @Override
    public PcmExperienceSimulationExecutor<PCMInstance, QVTOReconfigurator, QVToReconfiguration, Double> create() {
        Experiment experiment = loadExperiment();
        IExperimentProvider experimentProvider = createExperimentProvider(experiment);
        FaultTolerantVaryingInterarrivelRateProcess<PCMInstance, QVTOReconfigurator, QVToReconfiguration, Double> p = new FaultTolerantVaryingInterarrivelRateProcess<>(
                getDbn(), experimentProvider);
        EnvironmentProcess<QVTOReconfigurator, Double, List<InputValue<CategoricalValue>>> envProcess = p
            .getEnvironmentProcess();

        List<PcmMeasurementSpecification> pcmMeasurementSpecs = createSpecs(experiment);
        Set<SimulatedMeasurementSpecification> simulatedMeasurementSpecs = new HashSet<>(pcmMeasurementSpecs);
        SimulationRunnerHolder simulationRunnerHolder = createSimulationRunnerHolder();
        InitialPcmStateCreator<QVTOReconfigurator, List<InputValue<CategoricalValue>>> initialStateCreator = new InitialPcmStateCreator<>(
                simulatedMeasurementSpecs, experimentProvider, simulationRunnerHolder);

        List<ExperienceSimulationRunner> runners = List
            .of(new PerformabilityPcmExperienceSimulationRunner<>(experimentProvider, initialStateCreator));
        IQVToReconfigurationManager qvtoReconfigurationManager = createQvtoReconfigurationManager(experiment);
        Initializable beforeExecutionInitializable = new GlobalPcmBeforeExecutionInitialization(experimentProvider,
                qvtoReconfigurationManager);

        Pair<SimulatedMeasurementSpecification, Threshold> upperThresh = Pair.of(pcmMeasurementSpecs.get(0),
                Threshold.lessThanOrEqualTo(UPPER_THRESHOLD_RT.getValue()));
        Pair<SimulatedMeasurementSpecification, Threshold> lowerThresh = Pair.of(pcmMeasurementSpecs.get(0),
                Threshold.lessThanOrEqualTo(LOWER_THRESHOLD_RT.getValue()));
        RewardEvaluator<Double> evaluator = new PerformabilityRewardEvaluation(pcmMeasurementSpecs.get(0),
                pcmMeasurementSpecs.get(1), upperThresh, lowerThresh);

        PerformabilityStrategyConfiguration config = new PerformabilityStrategyConfiguration(SERVER_FAILURE_TEMPLATE_ID,
                LOAD_BALANCER_ID);

        // node recovery strategy
        NodeRecoveryStrategy<PCMInstance, QVTOReconfigurator> nodeRecoveryStrategy = new FaultTolerantScalingNodeFailureRecoveryStrategy<>(
                config, new RepositoryModelLookup(), new ResourceEnvironmentModelLookup(),
                new RepositoryModelUpdater());

        // configure the different planning strategies that shall be investigated by accordingly
        // (un)comment the required strategy definition: empty, scaling, fault-tolerant scaling
        LoadBalancingEmptyReconfigurationPlanningStrategy<PCMInstance, QVTOReconfigurator> emptyStrategy = new LoadBalancingEmptyReconfigurationPlanningStrategy<>(
                pcmMeasurementSpecs.get(0), config, nodeRecoveryStrategy);
        LoadBalancingScalingPlanningStrategy<PCMInstance> loadBalancingPlanningStrategy = new LoadBalancingScalingPlanningStrategy<>(
                pcmMeasurementSpecs.get(0), config, nodeRecoveryStrategy, LOWER_THRESHOLD_RT, UPPER_THRESHOLD_RT);
        FaultTolerantScalingPlanningStrategy<PCMInstance> ftLoadBalancingScalingPlanningStrategy = new FaultTolerantScalingPlanningStrategy<>(
                pcmMeasurementSpecs.get(0), config, nodeRecoveryStrategy, LOWER_THRESHOLD_RT, UPPER_THRESHOLD_RT);
        ReconfigurationPlanningStrategy reconfigurationPlanningStrategy = ftLoadBalancingScalingPlanningStrategy;

        ReconfigurationStrategy<QVTOReconfigurator, QVToReconfiguration> reconfStrategy = new PerformabilityStrategy<>(
                pcmMeasurementSpecs.get(0), config, reconfigurationPlanningStrategy);

        Policy<QVTOReconfigurator, QVToReconfiguration> reconfSelectionPolicy = reconfStrategy;

        Set<QVToReconfiguration> reconfigurations = new HashSet<>(qvtoReconfigurationManager.loadReconfigurations());

        ExperienceSimulator<PCMInstance, QVTOReconfigurator, Double> simulator = createExperienceSimulator(experiment,
                pcmMeasurementSpecs, runners, getSimulationParameters(), beforeExecutionInitializable, envProcess,
                getSimulatedExperienceStore(), null, reconfSelectionPolicy, reconfigurations, evaluator, false,
                experimentProvider, simulationRunnerHolder);

        // TODO: use from store
        String sampleSpaceId = SimulatedExperienceConstants
            .constructSampleSpaceId(getSimulationParameters().getSimulationID(), reconfSelectionPolicy.getId());
        TotalRewardCalculation rewardCalculation = new PerformabilityEvaluator(
                getSimulationParameters().getSimulationID(), sampleSpaceId);

        return new PcmExperienceSimulationExecutor<>(simulator, experiment, getSimulationParameters(),
                reconfSelectionPolicy, rewardCalculation, experimentProvider, qvtoReconfigurationManager);
    }
}
