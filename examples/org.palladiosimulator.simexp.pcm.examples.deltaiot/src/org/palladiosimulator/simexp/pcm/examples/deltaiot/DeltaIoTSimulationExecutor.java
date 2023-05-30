package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import static java.util.stream.Collectors.toSet;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.evaluation.SimulatedExperienceEvaluator;
import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.reward.ThresholdBasedRewardEvaluator;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.builder.PcmExperienceSimulationBuilder;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.init.GlobalPcmBeforeExecutionInitialization;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.prism.generator.PrismFileUpdateGenerator;
import org.palladiosimulator.simexp.pcm.prism.generator.PrismFileUpdateGenerator.PrismFileUpdater;
import org.palladiosimulator.simexp.pcm.prism.generator.PrismGenerator;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simexp.pcm.util.SimulationParameterConfiguration;

import com.google.common.collect.Sets;

import tools.mdsd.probdist.api.apache.supplier.MultinomialDistributionSupplier;
import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;
import tools.mdsd.probdist.api.parser.ParameterParser;

public class DeltaIoTSimulationExecutor extends PcmExperienceSimulationExecutor {
    
    private static final Logger LOGGER = Logger.getLogger(DeltaIoTSimulationExecutor.class.getName());

	public final static String DELTAIOT_PATH = "/org.palladiosimulator.envdyn.examples.deltaiot";

	private final static String DISTRIBUTION_FACTORS = DELTAIOT_PATH
			+ "/model/DeltaIoTReconfigurationParams.reconfigurationparams";
	private final static String PRISM_FOLDER = "prism";

	private final DeltaIoTReconfigurationParamRepository reconfParamsRepo;
	private final Policy<Action<?>> reconfSelectionPolicy;
	private final DynamicBayesianNetwork dbn;
	private final List<PrismSimulatedMeasurementSpec> prismSpecs;

	
	public DeltaIoTSimulationExecutor(Experiment experiment, DynamicBayesianNetwork dbn, IProbabilityDistributionRegistry probabilityDistributionRegistry,
			IProbabilityDistributionFactory probabilityDistributionFactory, ParameterParser parameterParser, 
			IProbabilityDistributionRepositoryLookup probDistRepoLookup, SimulationParameterConfiguration simulationParameters,
			List<PrismSimulatedMeasurementSpec> prismSpecs, IExperimentProvider experimentProvider, IQVToReconfigurationManager qvtoReconfigurationManager) {
		super(experiment, simulationParameters, experimentProvider, qvtoReconfigurationManager);
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

		this.dbn = dbn; //loadOrGenerateDBN();

		qvtoReconfigurationManager.addModelsToTransform(reconfParamsRepo.eResource());

//		DistributionTypeModelUtil.get(BasicDistributionTypesLoader.loadRepository());
//		ProbabilityDistributionFactory.get().register(new MultinomialDistributionSupplier());
	}

	public static final class DeltaIoTSimulationExecutorFactory {
	    public DeltaIoTSimulationExecutor create(Experiment experiment, DynamicBayesianNetwork dbn, 
	    		IProbabilityDistributionRegistry probabilityDistributionRegistry, IProbabilityDistributionFactory probabilityDistributionFactory, 
	    		ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup, 
	    		SimulationParameterConfiguration simulationParameters, List<PrismSimulatedMeasurementSpec> prismSpecs, IExperimentProvider experimentProvider, IQVToReconfigurationManager qvtoReconfigurationManager) {
	        return new DeltaIoTSimulationExecutor(experiment, dbn, probabilityDistributionRegistry, probabilityDistributionFactory, 
	        		parameterParser, probDistRepoLookup, simulationParameters, prismSpecs, experimentProvider, qvtoReconfigurationManager);
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


	@Override
	protected ExperienceSimulator createSimulator() {
		return PcmExperienceSimulationBuilder.newBuilder(experimentProvider, qvtoReconfigurationManager)
				.makeGlobalPcmSettings()
					.withInitialExperiment(experiment)
					.andSimulatedMeasurementSpecs(getPrismSpecs())
					.addExperienceSimulationRunner(getSimualtionRunner())
					.done()
				.createSimulationConfiguration()
					.withSimulationID(simulationParameters.getSimulationID()) // DeltaIoT
					.withNumberOfRuns(simulationParameters.getNumberOfRuns()) // 2
					.andNumberOfSimulationsPerRun(simulationParameters.getNumberOfSimulationsPerRun()) // 100
					.andOptionalExecutionBeforeEachRun(new GlobalPcmBeforeExecutionInitialization(experimentProvider, qvtoReconfigurationManager))
					.done()
				.specifySelfAdaptiveSystemState()
					.asPartiallyEnvironmentalDrivenProcess(DeltaIoTEnvironemtalDynamics.getPartiallyEnvironmentalDrivenProcess(dbn))
					.done()
				.createReconfigurationSpace()
					.addReconfigurations(getAllReconfigurations())
					.andReconfigurationStrategy(reconfSelectionPolicy)
					.done()
				.specifyRewardHandling()
					.withRewardEvaluator(getRewardEvaluator())
					.done()
				.build();
	}

	private ExperienceSimulationRunner getSimualtionRunner() {
		// return new PcmBasedPrismExperienceSimulationRunner(getPrismGenerator(),
		// createPrismLogFile());
		return new DeltaIoTPcmBasedPrismExperienceSimulationRunner(getPrismGenerator(), createPrismLogFile(),
				reconfParamsRepo, experimentProvider);
	}

	private File createPrismLogFile() {
		URI uri = URI.createPlatformResourceURI(Paths.get(DELTAIOT_PATH, PRISM_FOLDER).toString(), true);
		return new File(CommonPlugin.resolve(uri).toFileString());
	}

	private PrismGenerator getPrismGenerator() {
		return new PrismFileUpdateGenerator(getPrismFileUpdater());
	}

	private Set<PrismFileUpdater> getPrismFileUpdater() {
		return Sets.newHashSet(new PacketLossPrismFileUpdater(prismSpecs.get(0)),
				new EnergyConsumptionPrismFileUpdater(prismSpecs.get(1)));
	}

	private Set<Reconfiguration<?>> getAllReconfigurations() {
		Set<Reconfiguration<?>> reconfs = Sets.newHashSet();

		List<QVToReconfiguration> qvts = qvtoReconfigurationManager.loadReconfigurations();
		for (QVToReconfiguration each : qvts) {
			if (DistributionFactorReconfiguration.isCorrectQvtReconfguration(each)) {
				reconfs.add(new DistributionFactorReconfiguration(each, reconfParamsRepo.getDistributionFactors()));
			} else if (TransmissionPowerReconfiguration.isCorrectQvtReconfguration(each)) {
				reconfs.add(new TransmissionPowerReconfiguration(each, reconfParamsRepo.getTransmissionPower()));
			}
		}

		if (reconfs.isEmpty()) {
			// TODO exception handling
			throw new RuntimeException("No DeltaIoT reconfigutations could be found or generated");
		}
		return reconfs;
	}

	private RewardEvaluator getRewardEvaluator() {
		return ThresholdBasedRewardEvaluator.with(lowerPacketLossThreshold(), lowerEnergyConsumptionThreshold());
	}

	private Pair<SimulatedMeasurementSpecification, Threshold> lowerPacketLossThreshold() {
		return Pair.of(prismSpecs.get(0), GlobalQualityBasedReconfigurationStrategy.LOWER_PACKET_LOSS);
	}

	private Pair<SimulatedMeasurementSpecification, Threshold> lowerEnergyConsumptionThreshold() {
		return Pair.of(prismSpecs.get(1), GlobalQualityBasedReconfigurationStrategy.LOWER_ENERGY_CONSUMPTION);
	}

	private Set<SimulatedMeasurementSpecification> getPrismSpecs() {
		return prismSpecs.stream().map(SimulatedMeasurementSpecification.class::cast).collect(toSet());
	}

}
