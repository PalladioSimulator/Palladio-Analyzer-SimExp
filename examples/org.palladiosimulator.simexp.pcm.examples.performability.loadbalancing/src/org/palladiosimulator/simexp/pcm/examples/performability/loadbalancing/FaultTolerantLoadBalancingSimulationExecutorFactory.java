package org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.pcm.query.RepositoryModelLookup;
import org.palladiosimulator.pcm.query.ResourceEnvironmentModelLookup;
import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.evaluation.PerformabilityEvaluator;
import org.palladiosimulator.simexp.core.evaluation.TotalRewardCalculation;
import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.examples.performability.NodeRecoveryStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.PerformabilityRewardEvaluation;
import org.palladiosimulator.simexp.pcm.examples.performability.PerformabilityStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.PerformabilityStrategyConfiguration;
import org.palladiosimulator.simexp.pcm.examples.performability.RepositoryModelUpdater;
import org.palladiosimulator.simexp.pcm.init.GlobalPcmBeforeExecutionInitialization;
import org.palladiosimulator.simexp.pcm.process.PerformabilityPcmExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simexp.pcm.util.SimulationParameters;

import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;
import tools.mdsd.probdist.api.parser.ParameterParser;

public class FaultTolerantLoadBalancingSimulationExecutorFactory extends PcmExperienceSimulationExecutorFactory<PcmMeasurementSpecification> {
	public static final Threshold LOWER_THRESHOLD_RT = Threshold.greaterThanOrEqualTo(0.1);
    public static final Threshold UPPER_THRESHOLD_RT = Threshold.lessThanOrEqualTo(0.4);
	
    public static final String SERVER_FAILURE_TEMPLATE_ID = "_VtIJEPtrEeuPUtFH1XJrHw";
    public static final String LOAD_BALANCER_ID = "_NvLi8AEmEeS7FKokKTKFow";
	
	public FaultTolerantLoadBalancingSimulationExecutorFactory(Experiment experiment, DynamicBayesianNetwork dbn,
			List<PcmMeasurementSpecification> specs, SimulationParameters params,
			IProbabilityDistributionFactory distributionFactory,
			IProbabilityDistributionRegistry probabilityDistributionRegistry, ParameterParser parameterParser,
			IProbabilityDistributionRepositoryLookup probDistRepoLookup, IExperimentProvider experimentProvider) {
		super(experiment, dbn, specs, params, distributionFactory, probabilityDistributionRegistry, parameterParser,
				probDistRepoLookup, experimentProvider);
	}

	@Override
	@SuppressWarnings("unchecked")
	public PcmExperienceSimulationExecutor create() {
		List<ExperienceSimulationRunner> runners = List.of(new PerformabilityPcmExperienceSimulationRunner(experimentProvider));
		Initializable beforeExecutionInitializable = new GlobalPcmBeforeExecutionInitialization(experimentProvider);
		EnvironmentProcess envProcess = FaultTolerantVaryingInterarrivelRateProcess.get(dbn, experimentProvider);
			
		Pair<SimulatedMeasurementSpecification, Threshold> upperThresh = Pair.of(specs.get(0), 
				Threshold.lessThanOrEqualTo(UPPER_THRESHOLD_RT.getValue()));
		Pair<SimulatedMeasurementSpecification, Threshold> lowerThresh = Pair.of(specs.get(0), 
				Threshold.lessThanOrEqualTo(LOWER_THRESHOLD_RT.getValue()));
		RewardEvaluator evaluator = new PerformabilityRewardEvaluation(specs.get(0), specs.get(1), upperThresh, lowerThresh);
			
		PerformabilityStrategyConfiguration config = new PerformabilityStrategyConfiguration(SERVER_FAILURE_TEMPLATE_ID, LOAD_BALANCER_ID);
		NodeRecoveryStrategy nodeRecoveryStrategy = new FaultTolerantScalingNodeFailureRecoveryStrategy(config, new RepositoryModelLookup()
			     , new ResourceEnvironmentModelLookup(), new RepositoryModelUpdater());
		LoadBalancingEmptyReconfigurationPlanningStrategy emptyStrategy = new LoadBalancingEmptyReconfigurationPlanningStrategy(specs.get(0), config, nodeRecoveryStrategy);
		ReconfigurationStrategy<? extends Reconfiguration<?>> reconfStrategy = new PerformabilityStrategy(specs.get(0), config, emptyStrategy);
		Policy<Action<?>> reconfSelectionPolicy = (Policy<Action<?>>) reconfStrategy;
			
		Set<Reconfiguration<?>> reconfigurations = new HashSet<Reconfiguration<?>>(QVToReconfigurationManager.get().loadReconfigurations());
			
		ExperienceSimulator simulator = createExperienceSimulator(experiment, specs, runners, params, 
				beforeExecutionInitializable, envProcess, null, reconfSelectionPolicy, reconfigurations, evaluator, false);
			
		String sampleSpaceId = SimulatedExperienceConstants.constructSampleSpaceId(params.getSimulationID(), reconfSelectionPolicy.getId());
		TotalRewardCalculation rewardCalculation = new PerformabilityEvaluator(params.getSimulationID(), sampleSpaceId);
			
		return new PcmExperienceSimulationExecutor(simulator, experiment, params, reconfSelectionPolicy, rewardCalculation, experimentProvider);
	}
}
