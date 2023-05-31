package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.evaluation.SimulatedExperienceEvaluator;
import org.palladiosimulator.simexp.core.evaluation.TotalRewardCalculation;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.reward.ThresholdBasedRewardEvaluator;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.builder.PcmExperienceSimulationBuilder;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.init.GlobalPcmBeforeExecutionInitialization;
import org.palladiosimulator.simexp.pcm.process.PcmExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simexp.pcm.util.SimulationParameterConfiguration;

import com.google.common.collect.Sets;

import tools.mdsd.probdist.api.apache.supplier.MultinomialDistributionSupplier;
import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;
import tools.mdsd.probdist.api.parser.ParameterParser;

public class LoadBalancingSimulationExecutor extends PcmExperienceSimulationExecutor {
    
    private static final Logger LOGGER = Logger.getLogger(LoadBalancingSimulationExecutor.class.getName());

    public final static double UPPER_THRESHOLD_RT = 2.0;
	public final static double LOWER_THRESHOLD_RT = 0.3;
	
	private final static double THRESHOLD_UTIL_1 = 0.7;
	private final static double THRESHOLD_UTIL_2 = 0.5;
	
	private final DynamicBayesianNetwork dbn;
	private final List<PcmMeasurementSpecification> pcmSpecs;
	private final Policy<Action<?>> reconfSelectionPolicy;
	
	private LoadBalancingSimulationExecutor(Experiment experiment, DynamicBayesianNetwork dbn, 
			IProbabilityDistributionRegistry probabilityDistributionRegistry, 
			IProbabilityDistributionFactory probabilityDistributionFactory, ParameterParser parameterParser, 
			IProbabilityDistributionRepositoryLookup probDistRepoLookup, SimulationParameterConfiguration simulationParameters,
			List<PcmMeasurementSpecification> pcmSpecs, IExperimentProvider experimentProvider, IQVToReconfigurationManager qvtoReconfigurationManager) {
		super(experiment, simulationParameters, experimentProvider, qvtoReconfigurationManager);
		probabilityDistributionRegistry.register(new MultinomialDistributionSupplier(parameterParser, probDistRepoLookup));
		
		this.dbn = dbn;
		
		this.pcmSpecs = pcmSpecs;
		
//		this.reconfSelectionPolicy = new NonAdaptiveStrategy();
//		this.reconfSelectionPolicy = new RandomizedStrategy<Action<?>>();
		this.reconfSelectionPolicy = new NStepLoadBalancerStrategy(1, pcmSpecs.get(0));
//		this.reconfSelectionPolicy = new NStepLoadBalancerStrategy(2, pcmSpecs.get(0));
//		this.reconfSelectionPolicy = new LinearLoadBalancerStrategy(pcmSpecs.get(0));
	}
	
	public static final class LoadBalancingSimulationExecutorFactory {
	    public LoadBalancingSimulationExecutor create(Experiment experiment, DynamicBayesianNetwork dbn, 
	    		IProbabilityDistributionRegistry probabilityDistributionRegistry, IProbabilityDistributionFactory probabilityDistributionFactory, 
	    		ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup, 
	    		SimulationParameterConfiguration simulationParameters, List<PcmMeasurementSpecification> pcmSpecs, IExperimentProvider experimentProvider, IQVToReconfigurationManager qvtoReconfigurationManager) {
	        return new LoadBalancingSimulationExecutor(experiment, dbn, probabilityDistributionRegistry, probabilityDistributionFactory, 
	        		parameterParser, probDistRepoLookup, simulationParameters, pcmSpecs, experimentProvider, qvtoReconfigurationManager);
	    }
	}

	@Override
	public void evaluate() {
		String sampleSpaceId = SimulatedExperienceConstants.constructSampleSpaceId(simulationParameters.getSimulationID(), reconfSelectionPolicy.getId());
		TotalRewardCalculation evaluator = SimulatedExperienceEvaluator.of(simulationParameters.getSimulationID(), sampleSpaceId);
		LOGGER.info("***********************************************************************");
		LOGGER.info(String.format("The total Reward of policy %1s is %2s", reconfSelectionPolicy.getId(), evaluator.computeTotalReward()));
		LOGGER.info("***********************************************************************");
	}
	
	@Override
	protected ExperienceSimulator createSimulator() {
		return PcmExperienceSimulationBuilder.newBuilder(experimentProvider, qvtoReconfigurationManager)
				.makeGlobalPcmSettings()
					.withInitialExperiment(experiment)
					.andSimulatedMeasurementSpecs(Sets.newHashSet(pcmSpecs))
					.addExperienceSimulationRunner(new PcmExperienceSimulationRunner(experimentProvider))
					.done()
				.createSimulationConfiguration()
					.withSimulationID(simulationParameters.getSimulationID()) // LoadBalancing
					.withNumberOfRuns(simulationParameters.getNumberOfRuns()) //500
					.andNumberOfSimulationsPerRun(simulationParameters.getNumberOfSimulationsPerRun()) //100
					.andOptionalExecutionBeforeEachRun(new GlobalPcmBeforeExecutionInitialization(experimentProvider, qvtoReconfigurationManager))
					.done()
				.specifySelfAdaptiveSystemState()
				  	//.asEnvironmentalDrivenProcess(VaryingInterarrivelRateProcess.get())
				  	.asEnvironmentalDrivenProcess(VaryingInterarrivelRateProcess.get(dbn, experimentProvider))
					.done()
				.createReconfigurationSpace()
					.addReconfigurations(getAllReconfigurations())
				  	.andReconfigurationStrategy(reconfSelectionPolicy)
				  	.done()
				.specifyRewardHandling()
				  	.withRewardEvaluator(getSimpleRewardEvaluator())
				  	//.withRewardEvaluator(getRewardEvaluator())
				  	.done()
				.build();
	}

	private RewardEvaluator getSimpleRewardEvaluator() {
		return ThresholdBasedRewardEvaluator.with(upperResponseTimeThreshold());
			// lowerResponseTimeThreshold(),
			// cpuServer1Threshold(),
			// cpuServer2Threshold());
	}
	
	private RewardEvaluator getRewardEvaluator() {
		return new LoadBalancingRewardEvaluation(upperResponseTimeThreshold(), cpuServer1Threshold(), cpuServer2Threshold());
	}
	
	private Pair<SimulatedMeasurementSpecification, Threshold> upperResponseTimeThreshold() {
		return Pair.of(pcmSpecs.get(0), Threshold.lessThanOrEqualTo(UPPER_THRESHOLD_RT));
	}
	
	private Pair<SimulatedMeasurementSpecification, Threshold> lowerResponseTimeThreshold() {
		return Pair.of(pcmSpecs.get(0), Threshold.greaterThanOrEqualTo(LOWER_THRESHOLD_RT));
	}
	
	private Pair<SimulatedMeasurementSpecification, Threshold> cpuServer1Threshold() {
		return Pair.of(pcmSpecs.get(1), Threshold.lessThanOrEqualTo(THRESHOLD_UTIL_1));
	}

	private Pair<SimulatedMeasurementSpecification, Threshold> cpuServer2Threshold() {
		return Pair.of(pcmSpecs.get(2), Threshold.lessThanOrEqualTo(THRESHOLD_UTIL_2));
	}

	private Set<Reconfiguration<?>> getAllReconfigurations() {
		return new HashSet<Reconfiguration<?>>(qvtoReconfigurationManager.loadReconfigurations());
	}
}
