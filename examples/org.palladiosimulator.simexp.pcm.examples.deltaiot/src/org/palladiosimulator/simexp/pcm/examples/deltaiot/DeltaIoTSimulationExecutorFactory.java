package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.URI;
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
import org.palladiosimulator.simexp.core.statespace.SelfAdaptiveSystemStateSpaceNavigator;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutorFactory;
import org.palladiosimulator.simexp.pcm.init.GlobalPcmBeforeExecutionInitialization;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.prism.generator.PrismFileUpdateGenerator;
import org.palladiosimulator.simexp.pcm.prism.generator.PrismFileUpdateGenerator.PrismFileUpdater;
import org.palladiosimulator.simexp.pcm.prism.generator.PrismGenerator;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simexp.pcm.util.SimulationParameters;

import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;
import tools.mdsd.probdist.api.parser.ParameterParser;

public class DeltaIoTSimulationExecutorFactory extends PcmExperienceSimulationExecutorFactory<PrismSimulatedMeasurementSpec> {
	public final static String DELTAIOT_PATH = "/org.palladiosimulator.envdyn.examples.deltaiot";
	public final static String DISTRIBUTION_FACTORS = DELTAIOT_PATH + "/model/DeltaIoTReconfigurationParams.reconfigurationparams";
	public final static String PRISM_FOLDER = "prism";
	
	public DeltaIoTSimulationExecutorFactory(Experiment experiment, DynamicBayesianNetwork dbn,
			List<PrismSimulatedMeasurementSpec> specs, SimulationParameters params,
			IProbabilityDistributionFactory distributionFactory,
			IProbabilityDistributionRegistry probabilityDistributionRegistry, ParameterParser parameterParser,
			IProbabilityDistributionRepositoryLookup probDistRepoLookup, IExperimentProvider experimentProvider, IQVToReconfigurationManager qvtoReconfigurationManager) {
		super(experiment, dbn, specs, params, distributionFactory, probabilityDistributionRegistry, parameterParser,
				probDistRepoLookup, experimentProvider, qvtoReconfigurationManager);
	}

	@Override
	public PcmExperienceSimulationExecutor create() {
		URI uri = URI.createPlatformResourceURI(Paths.get(DELTAIOT_PATH, PRISM_FOLDER).toString(), true);
		File prismLogFile = new File(CommonPlugin.resolve(uri).toFileString());
		
		Set<PrismFileUpdater> prismFileUpdaters = new HashSet<>();
		prismFileUpdaters.add(new PacketLossPrismFileUpdater(specs.get(0)));
		prismFileUpdaters.add(new EnergyConsumptionPrismFileUpdater(specs.get(1)));
		PrismGenerator prismGenerator = new PrismFileUpdateGenerator(prismFileUpdaters);
		
		DeltaIoTReconfigurationParamRepository reconfParamsRepo = new DeltaIoTReconfigurationParamsLoader().load(DISTRIBUTION_FACTORS);
		qvtoReconfigurationManager.addModelsToTransform(reconfParamsRepo.eResource());
		
		ExperienceSimulationRunner runner = new DeltaIoTPcmBasedPrismExperienceSimulationRunner(prismGenerator, prismLogFile, reconfParamsRepo, experimentProvider);
		Initializable beforeExecutionInitializable = new GlobalPcmBeforeExecutionInitialization(experimentProvider, qvtoReconfigurationManager);
		SelfAdaptiveSystemStateSpaceNavigator navigator = DeltaIoTEnvironemtalDynamics.getPartiallyEnvironmentalDrivenProcess(dbn);
		
		Policy<Action<?>> reconfSelectionPolicy =  LocalQualityBasedReconfigurationStrategy.newBuilder()
				.withReconfigurationParams(reconfParamsRepo)
				.andPacketLossSpec(specs.get(0))
				.andEnergyConsumptionSpec(specs.get(1))
				.build();
		
		Pair<SimulatedMeasurementSpecification, Threshold> lowerPacketLossThreshold = Pair.of(specs.get(0), GlobalQualityBasedReconfigurationStrategy.LOWER_PACKET_LOSS);
		Pair<SimulatedMeasurementSpecification, Threshold> lowerEnergyConsumptionThreshold = Pair.of(specs.get(1), GlobalQualityBasedReconfigurationStrategy.LOWER_ENERGY_CONSUMPTION);
		RewardEvaluator evaluator = ThresholdBasedRewardEvaluator.with(lowerPacketLossThreshold, lowerEnergyConsumptionThreshold);
		
		Set<Reconfiguration<?>> reconfigurations = new HashSet<>();
		qvtoReconfigurationManager.loadReconfigurations().stream()
			.forEach(qvto -> {
				if (DistributionFactorReconfiguration.isCorrectQvtReconfguration(qvto)) {
					reconfigurations.add(new DistributionFactorReconfiguration(qvto, reconfParamsRepo.getDistributionFactors()));
				} else if (TransmissionPowerReconfiguration.isCorrectQvtReconfguration(qvto)) {
					reconfigurations.add(new TransmissionPowerReconfiguration(qvto, reconfParamsRepo.getTransmissionPower()));
				}
		});
		
		ExperienceSimulator simulator = createExperienceSimulator(experiment, specs, List.of(runner), params, 
				beforeExecutionInitializable, null, navigator, reconfSelectionPolicy,  reconfigurations, evaluator, false);
		
		String sampleSpaceId = SimulatedExperienceConstants.constructSampleSpaceId(params.getSimulationID(), reconfSelectionPolicy.getId());
			TotalRewardCalculation rewardCalculation = SimulatedExperienceEvaluator.of(params.getSimulationID(), sampleSpaceId);
			
		return new PcmExperienceSimulationExecutor(simulator, experiment, params, reconfSelectionPolicy, rewardCalculation, experimentProvider, qvtoReconfigurationManager);
	}
}