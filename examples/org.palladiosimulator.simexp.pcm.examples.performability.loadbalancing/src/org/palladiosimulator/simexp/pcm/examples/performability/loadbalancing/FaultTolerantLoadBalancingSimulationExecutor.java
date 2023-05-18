package org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing;

import java.util.List;

import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.pcm.query.RepositoryModelLookup;
import org.palladiosimulator.pcm.query.ResourceEnvironmentModelLookup;
import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.evaluation.PerformabilityEvaluator;
import org.palladiosimulator.simexp.core.evaluation.TotalRewardCalculation;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.examples.performability.NodeRecoveryStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.PerformabilityStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.PerformabilityStrategyConfiguration;
import org.palladiosimulator.simexp.pcm.examples.performability.ReconfigurationPlanningStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.RepositoryModelUpdater;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simexp.pcm.util.SimulationParameterConfiguration;

import tools.mdsd.probdist.api.apache.supplier.MultinomialDistributionSupplier;
import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;
import tools.mdsd.probdist.api.parser.ParameterParser;

public class FaultTolerantLoadBalancingSimulationExecutor extends PcmExperienceSimulationExecutor {
    
    public static final Threshold LOWER_THRESHOLD_RT = Threshold.greaterThanOrEqualTo(0.1);
    public static final Threshold UPPER_THRESHOLD_RT = Threshold.lessThanOrEqualTo(0.4);
	
    public static final String SERVER_FAILURE_TEMPLATE_ID = "_VtIJEPtrEeuPUtFH1XJrHw";
    public static final String LOAD_BALANCER_ID = "_NvLi8AEmEeS7FKokKTKFow";
	
	private final ReconfigurationStrategy<? extends Reconfiguration<?>> reconfSelectionPolicy;
	
    private final PcmMeasurementSpecification responseTimeMeasurementSpec;
    
	private final NodeRecoveryStrategy nodeRecoveryStrategy;
	private PerformabilityStrategyConfiguration strategyConfiguration;
	private final ReconfigurationPlanningStrategy reconfigurationPlanningStrategy;
		
	private FaultTolerantLoadBalancingSimulationExecutor(ExperienceSimulator experienceSimulator, Experiment experiment, DynamicBayesianNetwork dbn, 
			IProbabilityDistributionRegistry probabilityDistributionRegistry, 
			IProbabilityDistributionFactory probabilityDistributionFactory, ParameterParser parameterParser, 
			IProbabilityDistributionRepositoryLookup probDistRepoLookup, SimulationParameterConfiguration simulationParameters,
			List<PcmMeasurementSpecification> pcmSpecs) {
		super(experienceSimulator, experiment, simulationParameters);
		//this.dbn = dbn;
		//this.pcmSpecs = pcmSpecs;
		
		this.strategyConfiguration = new PerformabilityStrategyConfiguration(SERVER_FAILURE_TEMPLATE_ID, LOAD_BALANCER_ID);
		
		this.responseTimeMeasurementSpec = pcmSpecs.get(0);
        //this.systemResultExectutionTypeTimeMeasurementSpec = pcmSpecs.get(1);
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
	    public FaultTolerantLoadBalancingSimulationExecutor create(ExperienceSimulator experienceSimulator, Experiment experiment, DynamicBayesianNetwork dbn, 
	    		IProbabilityDistributionRegistry probabilityDistributionRegistry, IProbabilityDistributionFactory probabilityDistributionFactory, 
	    		ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup, SimulationParameterConfiguration simulationParameters,
	    		List<PcmMeasurementSpecification> pcmSpecs) {
	        return new FaultTolerantLoadBalancingSimulationExecutor(experienceSimulator, experiment, dbn, probabilityDistributionRegistry, probabilityDistributionFactory, 
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
}
