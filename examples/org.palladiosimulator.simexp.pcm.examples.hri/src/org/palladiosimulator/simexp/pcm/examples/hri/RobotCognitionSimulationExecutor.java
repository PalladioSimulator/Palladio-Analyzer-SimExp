package org.palladiosimulator.simexp.pcm.examples.hri;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.evaluation.ExpectedRewardEvaluator;
import org.palladiosimulator.simexp.core.evaluation.TotalRewardCalculation;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.init.GlobalPcmBeforeExecutionInitialization;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simexp.pcm.util.SimulationParameterConfiguration;

import tools.mdsd.probdist.api.apache.supplier.MultinomialDistributionSupplier;
import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;
import tools.mdsd.probdist.api.parser.ParameterParser;

public class RobotCognitionSimulationExecutor extends PcmExperienceSimulationExecutor {
    
    private static final Logger LOGGER = Logger.getLogger(RobotCognitionSimulationExecutor.class.getName());

	public static class RobotCognitionBeforeExecutionInitialization extends GlobalPcmBeforeExecutionInitialization {
		private final ReconfigurationStrategy<? extends Reconfiguration<?>> reconfigurationStrategy;
		
		public RobotCognitionBeforeExecutionInitialization(ReconfigurationStrategy<? extends Reconfiguration<?>> reconfigurationStrategy) {
			this.reconfigurationStrategy = reconfigurationStrategy;
		}
		
		@Override
		public void initialize() {
			super.initialize();
			
			if (reconfigurationStrategy instanceof Initializable) {
				Initializable.class.cast(reconfigurationStrategy).initialize();
			}
		}
		
	}
	
	public static final double UPPER_THRESHOLD_RT = 0.1;
	public static final double LOWER_THRESHOLD_REL = 0.9;
	
	public final static URI UNCERTAINTY_MODEL_URI = URI.createPlatformResourceURI("/org.palladiosimulator.dependability.ml.hri/RobotCognitionUncertaintyModel.uncertainty", true);
	
	private final ReconfigurationStrategy<? extends Reconfiguration<?>> reconfigurationStrategy;
	
	public RobotCognitionSimulationExecutor(ExperienceSimulator experienceSimulator, Experiment experiment, DynamicBayesianNetwork dbn, 
			IProbabilityDistributionRegistry probabilityDistributionRegistry, 
			IProbabilityDistributionFactory probabilityDistributionFactory, ParameterParser parameterParser, 
			IProbabilityDistributionRepositoryLookup probDistRepoLookup, SimulationParameterConfiguration simulationParameters,
			List<PcmMeasurementSpecification> pcmSpecs) {
	    super(experienceSimulator, experiment, simulationParameters);
		//this.reconfigurationStrategy = new ReliabilityPrioritizedStrategy(responseTimeSpec);
		//this.reconfigurationStrategy = new RandomizedAdaptationStrategy(responseTimeSpec);
		this.reconfigurationStrategy = new StaticSystemSimulation();
		
		probabilityDistributionRegistry.register(new MultinomialDistributionSupplier(parameterParser, probDistRepoLookup));
	}
	
	public static final class RobotCognitionSimulationExecutorFactory {
	    public RobotCognitionSimulationExecutor create(ExperienceSimulator experienceSimulator, Experiment experiment, DynamicBayesianNetwork dbn, 
	    		IProbabilityDistributionRegistry probabilityDistributionRegistry, 
	    		IProbabilityDistributionFactory probabilityDistributionFactory, ParameterParser parameterParser, 
	    		IProbabilityDistributionRepositoryLookup probDistRepoLookup, SimulationParameterConfiguration simulationParameters,
	    		List<PcmMeasurementSpecification> pcmSpecs) {
	        return new RobotCognitionSimulationExecutor(experienceSimulator, experiment, dbn, probabilityDistributionRegistry, probabilityDistributionFactory, 
	        		parameterParser, probDistRepoLookup, simulationParameters, pcmSpecs);
	    }
	}
	
	@Override
	public void evaluate() {
		String sampleSpaceId = SimulatedExperienceConstants.constructSampleSpaceId(simulationParameters.getSimulationID(), reconfigurationStrategy.getId());
		TotalRewardCalculation evaluator = new ExpectedRewardEvaluator(simulationParameters.getSimulationID(), sampleSpaceId);
		LOGGER.info("***********************************************************************");
		LOGGER.info(String.format("The total Reward of policy %1s is %2s", reconfigurationStrategy.getId(), evaluator.computeTotalReward()));
		LOGGER.info("***********************************************************************");
	}
}
