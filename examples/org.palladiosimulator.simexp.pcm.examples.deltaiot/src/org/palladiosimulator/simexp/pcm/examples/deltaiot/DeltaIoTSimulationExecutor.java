package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.evaluation.SimulatedExperienceEvaluator;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.util.SimulationParameterConfiguration;

import tools.mdsd.probdist.api.apache.supplier.MultinomialDistributionSupplier;
import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;
import tools.mdsd.probdist.api.parser.ParameterParser;

public class DeltaIoTSimulationExecutor extends PcmExperienceSimulationExecutor {
    
    private static final Logger LOGGER = Logger.getLogger(DeltaIoTSimulationExecutor.class.getName());

	public final static String DELTAIOT_PATH = "/org.palladiosimulator.envdyn.examples.deltaiot";

	public final static String DISTRIBUTION_FACTORS = DELTAIOT_PATH
			+ "/model/DeltaIoTReconfigurationParams.reconfigurationparams";
	public final static String PRISM_FOLDER = "prism";

	private final DeltaIoTReconfigurationParamRepository reconfParamsRepo;
	private final Policy<Action<?>> reconfSelectionPolicy;
	private final List<PrismSimulatedMeasurementSpec> prismSpecs;

	public DeltaIoTSimulationExecutor(ExperienceSimulator experienceSimulator, Experiment experiment, DynamicBayesianNetwork dbn, IProbabilityDistributionRegistry probabilityDistributionRegistry,
			IProbabilityDistributionFactory probabilityDistributionFactory, ParameterParser parameterParser, 
			IProbabilityDistributionRepositoryLookup probDistRepoLookup, SimulationParameterConfiguration simulationParameters,
			List<PrismSimulatedMeasurementSpec> prismSpecs) {
		super(experienceSimulator, experiment, simulationParameters);
		probabilityDistributionRegistry.register(new MultinomialDistributionSupplier(parameterParser, probDistRepoLookup));

		this.prismSpecs = prismSpecs;

		this.reconfParamsRepo = new DeltaIoTReconfigurationParamsLoader().load(DISTRIBUTION_FACTORS);
//		this.reconfSelectionPolicy = GlobalQualityBasedReconfigurationStrategy.newBuilder()
//				.withReconfigurationParams(reconfParamsRepo)
//				.andPacketLossSpec(this.prismSpecs.get(0))
//				.andEnergyConsumptionSpec(this.prismSpecs.get(1)).build();
		this.reconfSelectionPolicy = LocalQualityBasedReconfigurationStrategy.newBuilder()
				.withReconfigurationParams(reconfParamsRepo)
				.andPacketLossSpec(this.prismSpecs.get(0))
				.andEnergyConsumptionSpec(this.prismSpecs.get(1))
				.build();

		QVToReconfigurationManager.get().addModelsToTransform(reconfParamsRepo.eResource());

//		DistributionTypeModelUtil.get(BasicDistributionTypesLoader.loadRepository());
//		ProbabilityDistributionFactory.get().register(new MultinomialDistributionSupplier());
	}

	public static final class DeltaIoTSimulationExecutorFactory {
	    public DeltaIoTSimulationExecutor create(ExperienceSimulator experienceSimulator, Experiment experiment, DynamicBayesianNetwork dbn, 
	    		IProbabilityDistributionRegistry probabilityDistributionRegistry, IProbabilityDistributionFactory probabilityDistributionFactory, 
	    		ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup, 
	    		SimulationParameterConfiguration simulationParameters, List<PrismSimulatedMeasurementSpec> prismSpecs) {
	        return new DeltaIoTSimulationExecutor(experienceSimulator, experiment, dbn, probabilityDistributionRegistry, probabilityDistributionFactory, 
	        		parameterParser, probDistRepoLookup, simulationParameters, prismSpecs);
	    }
	}
	
	@Override
	public void evaluate() {
		String sampleSpaceId = SimulatedExperienceConstants.constructSampleSpaceId(simulationParameters.getSimulationID(),
				reconfSelectionPolicy.getId());
		Double totalReward = SimulatedExperienceEvaluator.of(simulationParameters.getSimulationID(), sampleSpaceId).computeTotalReward();
		LOGGER.info("***********************************************************************");
		LOGGER.info(String.format("The total Reward of policy %1s is %2s", reconfSelectionPolicy.getId(), totalReward));
		LOGGER.info("***********************************************************************");
	}
}
