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
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.builder.PcmExperienceSimulationBuilder;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.examples.performability.NodeRecoveryStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.PerformabilityRewardEvaluation;
import org.palladiosimulator.simexp.pcm.examples.performability.PerformabilityStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.PerformabilityStrategyConfiguration;
import org.palladiosimulator.simexp.pcm.examples.performability.ReconfigurationPlanningStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.RepositoryModelUpdater;
import org.palladiosimulator.simexp.pcm.init.GlobalPcmBeforeExecutionInitialization;
import org.palladiosimulator.simexp.pcm.process.PerformabilityPcmExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simexp.pcm.util.SimulationParameterConfiguration;

import com.google.common.collect.Sets;

import tools.mdsd.probdist.api.apache.supplier.MultinomialDistributionSupplier;
import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;
import tools.mdsd.probdist.api.parser.ParameterParser;

public class FaultTolerantLoadBalancingSimulationExecutor extends PcmExperienceSimulationExecutor {
    
    private static final Threshold LOWER_THRESHOLD_RT = Threshold.greaterThanOrEqualTo(0.1);
    private static final Threshold UPPER_THRESHOLD_RT = Threshold.lessThanOrEqualTo(0.4);
	
	private final static double THRESHOLD_UTIL_1 = 0.7;
	private final static double THRESHOLD_UTIL_2 = 0.5;
	
    private static final String SERVER_FAILURE_TEMPLATE_ID = "_VtIJEPtrEeuPUtFH1XJrHw";
    private static final String LOAD_BALANCER_ID = "_NvLi8AEmEeS7FKokKTKFow";
	
	private final DynamicBayesianNetwork dbn;
	private final List<PcmMeasurementSpecification> pcmSpecs;
	private final ReconfigurationStrategy<QVToReconfiguration> reconfSelectionPolicy;
	
    private final PcmMeasurementSpecification responseTimeMeasurementSpec;
    private final PcmMeasurementSpecification systemResultExectutionTypeTimeMeasurementSpec;
    
	private final NodeRecoveryStrategy nodeRecoveryStrategy;
	private PerformabilityStrategyConfiguration strategyConfiguration;
	private final ReconfigurationPlanningStrategy reconfigurationPlanningStrategy;
		
	private FaultTolerantLoadBalancingSimulationExecutor(Experiment experiment, DynamicBayesianNetwork dbn, 
			IProbabilityDistributionRegistry probabilityDistributionRegistry, 
			IProbabilityDistributionFactory probabilityDistributionFactory, ParameterParser parameterParser, 
			IProbabilityDistributionRepositoryLookup probDistRepoLookup, SimulationParameterConfiguration simulationParameters,
			List<PcmMeasurementSpecification> pcmSpecs) {
		super(experiment, simulationParameters);
		this.dbn = dbn;
		this.pcmSpecs = pcmSpecs;
		
		this.strategyConfiguration = new PerformabilityStrategyConfiguration(SERVER_FAILURE_TEMPLATE_ID, LOAD_BALANCER_ID);
		
		this.responseTimeMeasurementSpec = pcmSpecs.get(0);
        this.systemResultExectutionTypeTimeMeasurementSpec = pcmSpecs.get(1);
		this.nodeRecoveryStrategy = new FaultTolerantScalingNodeFailureRecoveryStrategy(strategyConfiguration, new RepositoryModelLookup()
		        , new ResourceEnvironmentModelLookup(), new RepositoryModelUpdater());

		// configure the different planning strategies that shall be investigated by accordingly (un)comment the required strategy definition
		this.reconfigurationPlanningStrategy = new LoadBalancingEmptyReconfigurationPlanningStrategy(responseTimeMeasurementSpec, strategyConfiguration, nodeRecoveryStrategy);
//        this.reconfigurationPlanningStrategy = new LoadBalancingScalingPlanningStrategy(responseTimeMeasurementSpec, strategyConfiguration
//                , nodeRecoveryStrategy , LOWER_THRESHOLD_RT, UPPER_THRESHOLD_RT);
//        this.reconfigurationPlanningStrategy = new FaultTolerantScalingPlanningStrategy(responseTimeMeasurementSpec, strategyConfiguration
//                , nodeRecoveryStrategy, LOWER_THRESHOLD_RT, UPPER_THRESHOLD_RT);

        this.reconfSelectionPolicy = new PerformabilityStrategy(responseTimeMeasurementSpec, strategyConfiguration, reconfigurationPlanningStrategy);
		
		probabilityDistributionRegistry.register(new MultinomialDistributionSupplier(parameterParser, probDistRepoLookup));
	}
	
	public static final class FaultTolerantLoadBalancingSimulationExecutorFactory {
	    public FaultTolerantLoadBalancingSimulationExecutor create(Experiment experiment, DynamicBayesianNetwork dbn, 
	    		IProbabilityDistributionRegistry probabilityDistributionRegistry, IProbabilityDistributionFactory probabilityDistributionFactory, 
	    		ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup, SimulationParameterConfiguration simulationParameters,
	    		List<PcmMeasurementSpecification> pcmSpecs) {
	        return new FaultTolerantLoadBalancingSimulationExecutor(experiment, dbn, probabilityDistributionRegistry, probabilityDistributionFactory, 
	        		parameterParser, probDistRepoLookup, simulationParameters, pcmSpecs);
	    }
	}

	@Override
	public void evaluate() {
		String sampleSpaceId = SimulatedExperienceConstants.constructSampleSpaceId(simulationParameters.getSimulationID(), reconfSelectionPolicy.getId());
		TotalRewardCalculation evaluator = new PerformabilityEvaluator(simulationParameters.getSimulationID(), sampleSpaceId);
		LOGGER.info("***********************************************************************");
		double computeTotalReward = evaluator.computeTotalReward();
        LOGGER.info(String.format("The total Reward of policy %1s is %2s", reconfSelectionPolicy.getId(), computeTotalReward));
		LOGGER.info("***********************************************************************");
	}
	
	@Override
	protected ExperienceSimulator createSimulator() {
		return PcmExperienceSimulationBuilder.newBuilder(experimentProvider)
				.makeGlobalPcmSettings()
					.withInitialExperiment(experiment)
					.andSimulatedMeasurementSpecs(Sets.newHashSet(pcmSpecs))
					.addExperienceSimulationRunner(new PerformabilityPcmExperienceSimulationRunner(experimentProvider))
					.done()
				.createSimulationConfiguration()
					.withSimulationID(simulationParameters.getSimulationID()) // LoadBalancing
					.withNumberOfRuns(simulationParameters.getNumberOfRuns()) //500
					.andNumberOfSimulationsPerRun(simulationParameters.getNumberOfSimulationsPerRun()) //100
					.andOptionalExecutionBeforeEachRun(new GlobalPcmBeforeExecutionInitialization(experimentProvider))
					.done()
				.specifySelfAdaptiveSystemState()
				  	.asEnvironmentalDrivenProcess(FaultTolerantVaryingInterarrivelRateProcess.get(dbn, experimentProvider))
					.done()
				.createReconfigurationSpace()
					.addReconfigurations(getAllReconfigurations())
				  	.andReconfigurationStrategy(reconfSelectionPolicy)
				  	.done()
				.specifyRewardHandling()
				  	.withRewardEvaluator(getPerformabilityRewardEvaluator())
				  	.done()
				.build();
	}

    private RewardEvaluator getPerformabilityRewardEvaluator() {
        return new PerformabilityRewardEvaluation(responseTimeMeasurementSpec, systemResultExectutionTypeTimeMeasurementSpec
                , lowerResponseTimeThreshold(), upperResponseTimeThreshold());
    }

	private Pair<SimulatedMeasurementSpecification, Threshold> upperResponseTimeThreshold() {
		return Pair.of(pcmSpecs.get(0), Threshold.lessThanOrEqualTo(UPPER_THRESHOLD_RT.getValue()));
	}
	
	private Pair<SimulatedMeasurementSpecification, Threshold> lowerResponseTimeThreshold() {
		return Pair.of(pcmSpecs.get(0), Threshold.greaterThanOrEqualTo(LOWER_THRESHOLD_RT.getValue()));
	}
	
	private Pair<SimulatedMeasurementSpecification, Threshold> cpuServer1Threshold() {
		return Pair.of(pcmSpecs.get(1), Threshold.lessThanOrEqualTo(THRESHOLD_UTIL_1));
	}

	private Pair<SimulatedMeasurementSpecification, Threshold> cpuServer2Threshold() {
		return Pair.of(pcmSpecs.get(2), Threshold.lessThanOrEqualTo(THRESHOLD_UTIL_2));
	}

	private Set<Reconfiguration<?>> getAllReconfigurations() {
		return new HashSet<Reconfiguration<?>>(QVToReconfigurationManager.get().loadReconfigurations());
	}
}
