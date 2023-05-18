package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.evaluation.SimulatedExperienceEvaluator;
import org.palladiosimulator.simexp.core.evaluation.TotalRewardCalculation;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simexp.pcm.util.SimulationParameterConfiguration;

import tools.mdsd.probdist.api.apache.supplier.MultinomialDistributionSupplier;
import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;
import tools.mdsd.probdist.api.parser.ParameterParser;

public class LoadBalancingSimulationExecutor extends PcmExperienceSimulationExecutor {
    
    private static final Logger LOGGER = Logger.getLogger(LoadBalancingSimulationExecutor.class.getName());

    public final static double UPPER_THRESHOLD_RT = 2.0;
	public final static double LOWER_THRESHOLD_RT = 0.3;
	
	private final Policy<Action<?>> reconfSelectionPolicy;
	private final boolean simulateWithUsageEvolution = true;
	
	private LoadBalancingSimulationExecutor(ExperienceSimulator experienceSimulator, Experiment experiment, DynamicBayesianNetwork dbn, 
			IProbabilityDistributionRegistry probabilityDistributionRegistry, 
			IProbabilityDistributionFactory probabilityDistributionFactory, ParameterParser parameterParser, 
			IProbabilityDistributionRepositoryLookup probDistRepoLookup, SimulationParameterConfiguration simulationParameters,
			List<PcmMeasurementSpecification> pcmSpecs) {
		super(experienceSimulator, experiment, simulationParameters);
		probabilityDistributionRegistry.register(new MultinomialDistributionSupplier(parameterParser, probDistRepoLookup));
		
		if (simulateWithUsageEvolution) {
			var usage = experiment.getInitialModel().getUsageEvolution().getUsages().get(0);
			var dynBehaviour = new UsageScenarioToDBNTransformer().transformAndPersist(usage);
			var bn = new BayesianNetwork(null, dynBehaviour.getModel(), probabilityDistributionFactory);
			//this.dbn = new DynamicBayesianNetwork(null, bn, dynBehaviour, probabilityDistributionFactory);
		} else {
		    //this.dbn = dbn;
		}
		
//		this.reconfSelectionPolicy = new NonAdaptiveStrategy();
//		this.reconfSelectionPolicy = new RandomizedStrategy<Action<?>>();
//		this.reconfSelectionPolicy = new NStepLoadBalancerStrategy(1, pcmSpecs.get(0));
		this.reconfSelectionPolicy = new NStepLoadBalancerStrategy(2, pcmSpecs.get(0));
//		this.reconfSelectionPolicy = new LinearLoadBalancerStrategy(pcmSpecs.get(0));
	}
	
	public static final class LoadBalancingSimulationExecutorFactory {
	    public LoadBalancingSimulationExecutor create(ExperienceSimulator experienceSimulator, Experiment experiment, DynamicBayesianNetwork dbn, 
	    		IProbabilityDistributionRegistry probabilityDistributionRegistry, IProbabilityDistributionFactory probabilityDistributionFactory, 
	    		ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup, 
	    		SimulationParameterConfiguration simulationParameters, List<PcmMeasurementSpecification> pcmSpecs) {
	        return new LoadBalancingSimulationExecutor(experienceSimulator, experiment, dbn, probabilityDistributionRegistry, probabilityDistributionFactory, 
	        		parameterParser, probDistRepoLookup, simulationParameters, pcmSpecs);
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
}
