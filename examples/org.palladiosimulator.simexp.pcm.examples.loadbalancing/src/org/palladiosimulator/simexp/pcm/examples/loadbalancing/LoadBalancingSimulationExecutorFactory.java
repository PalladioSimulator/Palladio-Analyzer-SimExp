package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.resource.ResourceSet;
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
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.config.IPCMWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.config.SimulationParameters;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.init.GlobalPcmBeforeExecutionInitialization;
import org.palladiosimulator.simexp.pcm.process.PcmExperienceSimulationRunner;
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

public class LoadBalancingSimulationExecutorFactory extends
        PcmExperienceSimulationExecutorFactory<Integer, List<InputValue<CategoricalValue>>, PcmMeasurementSpecification> {
    public final static double UPPER_THRESHOLD_RT = 2.0;
    public final static double LOWER_THRESHOLD_RT = 0.3;

    private final EnvironmentProcess<QVTOReconfigurator, Integer, List<InputValue<CategoricalValue>>> envProcess;
    private final InitialPcmStateCreator<QVTOReconfigurator, List<InputValue<CategoricalValue>>> initialStateCreator;

    public LoadBalancingSimulationExecutorFactory(IPCMWorkflowConfiguration workflowConfiguration, ResourceSet rs,
            Experiment experiment, DynamicBayesianNetwork<CategoricalValue> dbn,
            List<PcmMeasurementSpecification> specs, SimulationParameters params,
            SimulatedExperienceStore<QVTOReconfigurator, Integer> simulatedExperienceStore,
            IProbabilityDistributionFactory<CategoricalValue> distributionFactory,
            IProbabilityDistributionRegistry<CategoricalValue> probabilityDistributionRegistry,
            ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup,
            IExperimentProvider experimentProvider, SimulationRunnerHolder simulationRunnerHolder) {
        super(workflowConfiguration, rs, experiment, dbn, specs, params, simulatedExperienceStore, distributionFactory,
                probabilityDistributionRegistry, parameterParser, probDistRepoLookup, experimentProvider,
                simulationRunnerHolder);
        VaryingInterarrivelRateProcess<QVTOReconfigurator, QVToReconfiguration, Integer> p = new VaryingInterarrivelRateProcess<>(
                dbn, experimentProvider);
        this.envProcess = p.getEnvironmentProcess();

        Set<SimulatedMeasurementSpecification> simulatedMeasurementSpecs = new HashSet<>(specs);
        this.initialStateCreator = new InitialPcmStateCreator<>(simulatedMeasurementSpecs, experimentProvider,
                simulationRunnerHolder);
    }

    @Override
    public PcmExperienceSimulationExecutor<PCMInstance, QVTOReconfigurator, QVToReconfiguration, Integer> create() {
        List<ExperienceSimulationRunner> simulationRunners = List
            .of(new PcmExperienceSimulationRunner<>(getExperimentProvider(), initialStateCreator));
        IQVToReconfigurationManager qvtoReconfigurationManager = createQvtoReconfigurationManager();
        Initializable beforeExecutionInitializable = new GlobalPcmBeforeExecutionInitialization(getExperimentProvider(),
                qvtoReconfigurationManager);
        Policy<QVTOReconfigurator, QVToReconfiguration> reconfSelectionPolicy = new NStepLoadBalancerStrategy<PCMInstance, QVTOReconfigurator>(
                1, getSpecs().get(0), UPPER_THRESHOLD_RT, LOWER_THRESHOLD_RT);

        Pair<SimulatedMeasurementSpecification, Threshold> threshold = Pair.of(getSpecs().get(0),
                Threshold.lessThanOrEqualTo(UPPER_THRESHOLD_RT));
        RewardEvaluator<Integer> evaluator = ThresholdBasedRewardEvaluator.with(threshold);

        Set<QVToReconfiguration> reconfigurations = new HashSet<>(qvtoReconfigurationManager.loadReconfigurations());

        ExperienceSimulator<PCMInstance, QVTOReconfigurator, Integer> simulator = createExperienceSimulator(
                getExperiment(), getSpecs(), simulationRunners, getSimulationParameters(), beforeExecutionInitializable,
                envProcess, getSimulatedExperienceStore(), null, reconfSelectionPolicy, reconfigurations, evaluator,
                false);

        String sampleSpaceId = SimulatedExperienceConstants
            .constructSampleSpaceId(getSimulationParameters().getSimulationID(), reconfSelectionPolicy.getId());
        TotalRewardCalculation rewardCalculation = SimulatedExperienceEvaluator
            .of(getSimulationParameters().getSimulationID(), sampleSpaceId);

        return new PcmExperienceSimulationExecutor<>(simulator, getExperiment(), getSimulationParameters(),
                reconfSelectionPolicy, rewardCalculation, getExperimentProvider(), qvtoReconfigurationManager);
    }
}
