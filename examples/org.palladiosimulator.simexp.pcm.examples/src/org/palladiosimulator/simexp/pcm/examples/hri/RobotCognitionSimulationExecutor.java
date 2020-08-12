package org.palladiosimulator.simexp.pcm.examples.hri;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.palladiosimulator.dependability.reliability.uncertainty.UncertaintyFailureTypeRepository;
import org.palladiosimulator.dependability.reliability.uncertainty.UncertaintyInducedFailureType;
import org.palladiosimulator.dependability.reliability.uncertainty.UncertaintyPackage;
import org.palladiosimulator.dependability.reliability.uncertainty.solver.api.UncertaintyBasedReliabilityPredictionConfig;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.monitorrepository.Monitor;
import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.evaluation.SimulatedExperienceEvaluator;
import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.builder.PcmExperienceSimulationBuilder;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.process.PcmExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.reliability.entity.PcmRelSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.reliability.process.PcmRelExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.solver.runconfig.PCMSolverWorkflowRunConfiguration;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class RobotCognitionSimulationExecutor extends PcmExperienceSimulationExecutor {

	private final static String EXPERIMENT_FILE = "/";
	private final static String SIMULATION_ID = "Robot Cognition";
	private final static String RESPONSE_TIME_MONITOR = "System Response Time";
	private final static URI UNCERTAINTY_MODEL_URI = URI.createURI("");
	private static final double UPPER_THRESHOLD_RT = 1.0;
	private static final double LOWER_THRESHOLD_REL = 0.95;
	
	private final DynamicBayesianNetwork dbn;
	private final List<SimulatedMeasurementSpecification> pcmSpecs;
	private final Policy<Action<?>> reconfigurationStrategy;
	
	public RobotCognitionSimulationExecutor() {
		this.dbn = RobotCognitionDBNLoader.load();
		this.pcmSpecs = createSimMeasurementSpecs();
		this.reconfigurationStrategy = new RobotCognitionReconfigurationStrategy(pcmSpecs.get(1), pcmSpecs.get(0));
	}
	
	@Override
	public void evaluate() {
		String sampleSpaceId = SimulatedExperienceConstants.constructSampleSpaceId(SIMULATION_ID, reconfigurationStrategy.getId());
		SimulatedExperienceEvaluator evaluator = SimulatedExperienceEvaluator.of(SIMULATION_ID, sampleSpaceId);
		System.out.println("***********************************************************************");
		System.out.println(String.format("The total Reward of policy %1s is %2s", reconfigurationStrategy.getId(), evaluator.computeTotalReward()));
		System.out.println("***********************************************************************");
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
					.andSimulatedMeasurementSpecs(Sets.newHashSet(pcmSpecs))
					.addExperienceSimulationRunner(createPcmRelExperienceSimulationRunner())
					.addExperienceSimulationRunner(new PcmExperienceSimulationRunner())
					.done()
				.createSimulationConfiguration()
					.withSimulationID(SIMULATION_ID)
					.withNumberOfRuns(3) //500
					.andNumberOfSimulationsPerRun(5) //100
					.done()
				.specifySelfAdaptiveSystemState()
				  	.asEnvironmentalDrivenProcess(RobotCognitionEnvironmentalDynamics.get(dbn))
					.done()
				.createReconfigurationSpace()
					.addReconfigurations(getAllReconfigurations())
				  	.andReconfigurationSelectionPolicy(reconfigurationStrategy)
				  	.done()
				.specifyRewardHandling()
				  	.withRewardEvaluator(getRewardEvaluator())
				  	.done()
				.build();
	}
	
	private Set<Reconfiguration<?>> getAllReconfigurations() {
		return Sets.newHashSet(QVToReconfigurationManager.get().loadReconfigurations());
	}
	
	private RewardEvaluator getRewardEvaluator() {
		return new RobotCognitionRewardEvaluation(responseTimeThreshold(), reliabilityThreshold());
	}

	private Pair<SimulatedMeasurementSpecification, Threshold> responseTimeThreshold() {
		return Pair.of(pcmSpecs.get(0), Threshold.lessThanOrEqualTo(UPPER_THRESHOLD_RT));
	}
	
	private Pair<SimulatedMeasurementSpecification, Threshold> reliabilityThreshold() {
		return Pair.of(pcmSpecs.get(1), Threshold.greaterThanOrEqualTo(LOWER_THRESHOLD_REL));
	}

	private ExperienceSimulationRunner createPcmRelExperienceSimulationRunner() {
		return new PcmRelExperienceSimulationRunner(createDefaultReliabilityConfig());
	}

	private List<SimulatedMeasurementSpecification> createSimMeasurementSpecs() {
		return Lists.newArrayList(buildResponseTimeSpec(), buildReliabilitySpec());
	}
	
	private SimulatedMeasurementSpecification buildReliabilitySpec() {
		var usageScenario = experiment.getInitialModel().getUsageModel().getUsageScenario_UsageModel().get(0);
		return new PcmRelSimulatedMeasurementSpec(usageScenario);
	}

	private SimulatedMeasurementSpecification buildResponseTimeSpec() {
		Monitor rtMonitor = findMonitor(RESPONSE_TIME_MONITOR);
		MeasurementSpecification rtSpec = rtMonitor.getMeasurementSpecifications().get(0);
		return PcmMeasurementSpecification.newBuilder()
				.withName(rtMonitor.getEntityName())
				.measuredAt(rtMonitor.getMeasuringPoint())
				.withMetric(rtSpec.getMetricDescription())
				.useDefaultMeasurementAggregation()
				.build();
	}
	
	private Monitor findMonitor(String monitorName) {
		Stream<Monitor> monitors = experiment.getInitialModel().getMonitorRepository().getMonitors().stream();
		return monitors.filter(m -> m.getEntityName().equals(monitorName))
					   .findFirst()
					   .orElseThrow(() -> new RuntimeException("There is no monitor."));
	}
	
	private UncertaintyBasedReliabilityPredictionConfig createDefaultReliabilityConfig() {
		return new UncertaintyBasedReliabilityPredictionConfig(createDefaultRunConfig(),
				null, loadUncertainties(), null);
	}
	
	private List<UncertaintyInducedFailureType> loadUncertainties() {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		rs.getPackageRegistry().put(UncertaintyPackage.eNS_URI, UncertaintyPackage.eINSTANCE);

		var resource = rs.getResource(UNCERTAINTY_MODEL_URI, true);
		EcoreUtil.resolveAll(rs);

		var repo = (UncertaintyFailureTypeRepository) resource.getContents().get(0);
		return repo.getUncertaintyInducedFailureTypes();
	}

	private PCMSolverWorkflowRunConfiguration createDefaultRunConfig() {
		var config = new PCMSolverWorkflowRunConfiguration();
        config.setReliabilityAnalysis(true);
        config.setPrintMarkovStatistics(false);
        config.setPrintMarkovSingleResults(false);
        config.setSensitivityModelEnabled(false);
        config.setSensitivityModelFileName(null);
        config.setSensitivityLogFileName(null);
        // TODO check
        config.setLogFile(null);
        config.setNumberOfEvaluatedSystemStatesEnabled(false);
        config.setNumberOfEvaluatedSystemStates(0);
        config.setNumberOfExactDecimalPlacesEnabled(false);
        config.setNumberOfExactDecimalPlaces(0);
        config.setSolvingTimeLimitEnabled(false);
        config.setMarkovModelStorageEnabled(false);
        config.setIterationOverPhysicalSystemStatesEnabled(true);
        // TODO check
        config.setMarkovEvaluationMode("POINTSOFFAILURE"); 
        config.setSaveResultsToFileEnabled(false);
        return config;
	}

}
