package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import static java.util.stream.Collectors.toSet;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.api.generator.BayesianNetworkGenerator;
import org.palladiosimulator.envdyn.api.generator.DynamicBayesianNetworkGenerator;
import org.palladiosimulator.envdyn.environment.templatevariable.TemplateVariableDefinitions;
import org.palladiosimulator.envdyn.environment.templatevariable.TemplatevariablePackage;
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
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.builder.PcmExperienceSimulationBuilder;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.init.GlobalPcmBeforeExecutionInitialization;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.prism.generator.PrismFileUpdateGenerator;
import org.palladiosimulator.simexp.pcm.prism.generator.PrismFileUpdateGenerator.PrismFileUpdater;
import org.palladiosimulator.simexp.pcm.prism.generator.PrismGenerator;
import org.palladiosimulator.simexp.pcm.util.ExperimentProvider;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import tools.mdsd.probdist.api.apache.supplier.MultinomialDistributionSupplier;
import tools.mdsd.probdist.api.apache.util.DistributionTypeModelUtil;
import tools.mdsd.probdist.api.factory.ProbabilityDistributionFactory;
import tools.mdsd.probdist.model.basic.loader.BasicDistributionTypesLoader;

public class DeltaIoTSimulationExecutor extends PcmExperienceSimulationExecutor {
    
    private static final Logger LOGGER = Logger.getLogger(DeltaIoTSimulationExecutor.class.getName());

	public final static String DELTAIOT_PATH = "/org.palladiosimulator.envdyn.examples.deltaiot";

	private final static String DISTRIBUTION_FACTORS = DELTAIOT_PATH
			+ "/model/DeltaIoTReconfigurationParams.reconfigurationparams";
	private final static String SIMULATION_ID = "DeltaIoT";
	private final static String EXPERIMENT_FILE = DELTAIOT_PATH + "/model/DeltaIoTExperiment.experiments";
	private final static String PRISM_FOLDER = "prism";
	private final static String PRISM_PACKET_LOSS_MODULE_NAME = "PacketLoss.prism";
	private final static String PRISM_PACKET_LOSS_PROPERTY_NAME = "PacketLoss.props";
	private final static String PRISM_PACKET_LOSS_PROPERTY = "P=? [ F \"Packetloss\" ]";
	private final static String PRISM_ENERGY_CONSUMPTION_MODULE_NAME = "EnergyConsumption.prism";
	private final static String PRISM_ENERGY_CONSUMPTION_PROPERTY_NAME = "EnergyConsumption.props";
	private final static String PRISM_ENERGY_CONSUMPTION_PROPERTY = "Rmax=? [ F \"EnergyConsumption\" ]";

	private final DeltaIoTReconfigurationParamRepository reconfParamsRepo;
	private final Policy<Action<?>> reconfSelectionPolicy;
	private final DynamicBayesianNetwork dbn;
	private final List<PrismSimulatedMeasurementSpec> prismSpecs;

	public DeltaIoTSimulationExecutor() {
		this.prismSpecs = Lists.newArrayList();
		this.prismSpecs.add(createPrismSimulatedMeasurementSpecificationForPacketLoss());
		this.prismSpecs.add(createPrismSimulatedMeasurementSpecificationForEnergyConsumption());

		this.reconfParamsRepo = new DeltaIoTReconfigurationParamsLoader().load(DISTRIBUTION_FACTORS);
//		this.reconfSelectionPolicy = GlobalQualityBasedReconfigurationStrategy.newBuilder()
//				.withReconfigurationParams(reconfParamsRepo)
//				.andPacketLossSpec(this.prismSpecs.get(0))
//				.andEnergyConsumptionSpec(this.prismSpecs.get(1)).build();
		this.reconfSelectionPolicy = LocalQualityBasedReconfigurationStrategy.newBuilder()
				.withReconfigurationParams(reconfParamsRepo).andPacketLossSpec(this.prismSpecs.get(0))
				.andEnergyConsumptionSpec(this.prismSpecs.get(1)).build();

		this.dbn = loadOrGenerateDBN();

		QVToReconfigurationManager.get().addModelsToTransform(reconfParamsRepo.eResource());

		DistributionTypeModelUtil.get(BasicDistributionTypesLoader.loadRepository());
		ProbabilityDistributionFactory.get().register(new MultinomialDistributionSupplier());
	}

	private DynamicBayesianNetwork loadOrGenerateDBN() {
		try {
			return DeltaIoTDBNLoader.loadDBN();
		} catch (Exception e) {
			return generateDBN();
		}
	}

	private DynamicBayesianNetwork generateDBN() {
		TemplateVariableDefinitions templates = loadTemplates();

		BayesianNetwork bn = null;
		try {
			bn = new BayesianNetwork(null, DeltaIoTDBNLoader.loadGroundProbabilisticNetwork());
		} catch (Exception e) {
			bn = generateBN(templates);
			DeltaIoTDBNLoader.persist(bn.get(), DELTAIOT_PATH + "/model/DeltaIoTNonTemporalEnvironment.staticmodel");
		}

		DynamicBayesianNetwork dbn = new DynamicBayesianNetworkGenerator(templates)
				.createProbabilisticNetwork(bn.get());

		DeltaIoTDBNLoader.persist(dbn.getDynamics(),
				DELTAIOT_PATH + "/model/DeltaIoTEnvironmentalDynamics.dynamicmodel");

		return dbn;
	}

	private BayesianNetwork generateBN(TemplateVariableDefinitions templates) {
		ResourceSet appliedModels = new ResourceSetImpl();
		appliedModels.getResources().add(experiment.getInitialModel().getSystem().eResource());
		appliedModels.getResources().add(experiment.getInitialModel().getResourceEnvironment().eResource());
		return new BayesianNetworkGenerator(templates).generate(appliedModels);
	}

	private TemplateVariableDefinitions loadTemplates() {
		List<TemplateVariableDefinitions> result = ExperimentProvider.get().getExperimentRunner()
				.getWorkingPartition()
				.getElement(TemplatevariablePackage.eINSTANCE.getTemplateVariableDefinitions());
		if (result.isEmpty()) {
			// TODO exception handling
			throw new RuntimeException("There are no templates.");
		}
		return result.get(0);
	}

	@Override
	public void evaluate() {
		String sampleSpaceId = SimulatedExperienceConstants.constructSampleSpaceId(SIMULATION_ID,
				reconfSelectionPolicy.getId());
		Double totalReward = SimulatedExperienceEvaluator.of(SIMULATION_ID, sampleSpaceId).computeTotalReward();
		LOGGER.info("***********************************************************************");
		LOGGER.info(String.format("The total Reward of policy %1s is %2s", reconfSelectionPolicy.getId(), totalReward));
		LOGGER.info("***********************************************************************");
	}

	@Override
	protected String getExperimentFile() {
		return EXPERIMENT_FILE;
	}

	@Override
	protected ExperienceSimulator createSimulator() {
		return PcmExperienceSimulationBuilder.newBuilder()
				.makeGlobalPcmSettings()
					.withInitialExperiment(experiment)
					.andSimulatedMeasurementSpecs(getPrismSpecs())
					.addExperienceSimulationRunner(getSimualtionRunner())
					.done()
				.createSimulationConfiguration()
					.withSimulationID(SIMULATION_ID)
					.withNumberOfRuns(2)
					.andNumberOfSimulationsPerRun(100)
					.andOptionalExecutionBeforeEachRun(new GlobalPcmBeforeExecutionInitialization())
					.done()
				.specifySelfAdaptiveSystemState()
					.asPartiallyEnvironmentalDrivenProcess(DeltaIoTEnvironemtalDynamics.getPartiallyEnvironmentalDrivenProcess(dbn))
					.done()
				.createReconfigurationSpace()
					.addReconfigurations(getAllReconfigurations())
					.andReconfigurationSelectionPolicy(reconfSelectionPolicy)
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
				reconfParamsRepo);
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

		List<QVToReconfiguration> qvts = QVToReconfigurationManager.get().loadReconfigurations();
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

	private PrismSimulatedMeasurementSpec createPrismSimulatedMeasurementSpecificationForPacketLoss() {
		return PrismSimulatedMeasurementSpec.newBuilder().withProperty(PRISM_PACKET_LOSS_PROPERTY)
				.andModuleFile(getPacketLossModuleFile()).andPropertyFile(getPacketLossPropertyFile()).build();
	}

	private PrismSimulatedMeasurementSpec createPrismSimulatedMeasurementSpecificationForEnergyConsumption() {
		return PrismSimulatedMeasurementSpec.newBuilder().withProperty(PRISM_ENERGY_CONSUMPTION_PROPERTY)
				.andModuleFile(getEnergyConsumptionModuleFile()).andPropertyFile(getEnergyConsumptionPropertyFile())
				.build();
	}

	private File getPacketLossPropertyFile() {
		return resolvePrismFile(PRISM_PACKET_LOSS_PROPERTY_NAME);
	}

	private File getPacketLossModuleFile() {
		return resolvePrismFile(PRISM_PACKET_LOSS_MODULE_NAME);
	}

	private File getEnergyConsumptionPropertyFile() {
		return resolvePrismFile(PRISM_ENERGY_CONSUMPTION_PROPERTY_NAME);
	}

	private File getEnergyConsumptionModuleFile() {
		return resolvePrismFile(PRISM_ENERGY_CONSUMPTION_MODULE_NAME);
	}

	private File resolvePrismFile(String prismFileName) {
		URI uri = URI.createPlatformResourceURI(Paths.get(DELTAIOT_PATH, PRISM_FOLDER, prismFileName).toString(), true);
		return new File(CommonPlugin.resolve(uri).toFileString());
	}

	private Set<SimulatedMeasurementSpecification> getPrismSpecs() {
		return prismSpecs.stream().map(SimulatedMeasurementSpecification.class::cast).collect(toSet());
	}

}
