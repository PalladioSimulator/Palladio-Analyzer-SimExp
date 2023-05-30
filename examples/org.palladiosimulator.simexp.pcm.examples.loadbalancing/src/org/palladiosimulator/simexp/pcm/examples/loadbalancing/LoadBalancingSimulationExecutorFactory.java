package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.evaluation.SimulatedExperienceEvaluator;
import org.palladiosimulator.simexp.core.evaluation.TotalRewardCalculation;
import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.reward.ThresholdBasedRewardEvaluator;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.init.GlobalPcmBeforeExecutionInitialization;
import org.palladiosimulator.simexp.pcm.process.PcmExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simexp.pcm.util.SimulationParameters;

import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;
import tools.mdsd.probdist.api.parser.ParameterParser;

public class LoadBalancingSimulationExecutorFactory extends PcmExperienceSimulationExecutorFactory<PcmMeasurementSpecification> {
	public final static double UPPER_THRESHOLD_RT = 2.0;
	public final static double LOWER_THRESHOLD_RT = 0.3;
	
	public LoadBalancingSimulationExecutorFactory(Experiment experiment, DynamicBayesianNetwork dbn,
			List<PcmMeasurementSpecification> specs, SimulationParameters params,
			IProbabilityDistributionFactory distributionFactory,
			IProbabilityDistributionRegistry probabilityDistributionRegistry, ParameterParser parameterParser,
			IProbabilityDistributionRepositoryLookup probDistRepoLookup, IExperimentProvider experimentProvider) {
		super(experiment, dbn, specs, params, distributionFactory, probabilityDistributionRegistry, parameterParser,
				probDistRepoLookup, experimentProvider);
	}
	
	@Override
	public PcmExperienceSimulationExecutor create() {
		List<ExperienceSimulationRunner> simulationRunners = List.of(new PcmExperienceSimulationRunner(experimentProvider));
		Initializable beforeExecutionInitializable = new GlobalPcmBeforeExecutionInitialization(experimentProvider);
		Policy<Action<?>> reconfSelectionPolicy = new NStepLoadBalancerStrategy(2, specs.get(0), UPPER_THRESHOLD_RT, LOWER_THRESHOLD_RT);
			
		Pair<SimulatedMeasurementSpecification, Threshold> threshold = Pair.of(specs.get(0), 
				Threshold.lessThanOrEqualTo(UPPER_THRESHOLD_RT));
		RewardEvaluator evaluator = ThresholdBasedRewardEvaluator.with(threshold);
				
		boolean simulateWithUsageEvolution = true; // FIXME
		EnvironmentProcess envProcess;
		if (simulateWithUsageEvolution) {
			var usage = experiment.getInitialModel().getUsageEvolution().getUsages().get(0);
			var dynBehaviour = new UsageScenarioToDBNTransformer().transformAndPersist(usage);
			var bn = new BayesianNetwork(null, dynBehaviour.getModel(), distributionFactory);
		envProcess = VaryingInterarrivelRateProcess.get(new DynamicBayesianNetwork(null, bn, dynBehaviour, distributionFactory), experimentProvider);
		} else {
			envProcess = VaryingInterarrivelRateProcess.get(dbn, experimentProvider);
		}
		
		Set<Reconfiguration<?>> reconfigurations = new HashSet<Reconfiguration<?>>(QVToReconfigurationManager.get().loadReconfigurations());
			
		ExperienceSimulator simulator = createExperienceSimulator(experiment, specs, simulationRunners, 
				params, beforeExecutionInitializable, envProcess, null, reconfSelectionPolicy, reconfigurations, evaluator, false);
				
		String sampleSpaceId = SimulatedExperienceConstants.constructSampleSpaceId(params.getSimulationID(), reconfSelectionPolicy.getId());
		TotalRewardCalculation rewardCalculation = SimulatedExperienceEvaluator.of(params.getSimulationID(), sampleSpaceId);
		
		return new PcmExperienceSimulationExecutor(simulator, experiment, params, reconfSelectionPolicy, rewardCalculation, experimentProvider);
	}
}
