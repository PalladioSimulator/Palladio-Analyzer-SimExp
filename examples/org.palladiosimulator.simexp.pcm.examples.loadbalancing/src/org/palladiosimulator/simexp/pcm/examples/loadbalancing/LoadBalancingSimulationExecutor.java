package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.monitorrepository.Monitor;
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
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.builder.PcmExperienceSimulationBuilder;
import org.palladiosimulator.simexp.pcm.datasource.MeasurementSeriesResult.MeasurementSeries;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.examples.measurements.aggregator.UtilizationAggregator;
import org.palladiosimulator.simexp.pcm.init.GlobalPcmBeforeExecutionInitialization;
import org.palladiosimulator.simexp.pcm.process.PcmExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification.MeasurementAggregator;

import com.google.common.collect.Sets;

import tools.mdsd.probdist.api.apache.supplier.MultinomialDistributionSupplier;
import tools.mdsd.probdist.api.apache.util.DistributionTypeModelUtil;
import tools.mdsd.probdist.api.factory.ProbabilityDistributionFactory;
import tools.mdsd.probdist.model.basic.loader.BasicDistributionTypesLoader;

public class LoadBalancingSimulationExecutor extends PcmExperienceSimulationExecutor {
    
    private static final Logger LOGGER = Logger.getLogger(LoadBalancingSimulationExecutor.class.getName());

	public final static double UPPER_THRESHOLD_RT = 2.0;
	public final static double LOWER_THRESHOLD_RT = 1.0;
	
	private final static String EXPERIMENT_FILE = "/org.palladiosimulator.simexp.pcm.examples.loadbalancer/elasticity.experiments";
	private final static String SIMULIZAR_EXPERIMENT_FILE = "/org.palladiosimulator.simexp.pcm.examples.loadbalancer/simExpExperiments/simuLizarElasticity.experiments";
	private final static double THRESHOLD_UTIL_1 = 0.7;
	private final static double THRESHOLD_UTIL_2 = 0.5;
	private final static String RESPONSE_TIME_MONITOR = "System Response Time";
	private final static String CPU_SERVER_1_MONITOR = "cpuServer1";
	private final static String CPU_SERVER_2_MONITOR = "cpuServer2";
	private final static String SIMULATION_ID = "LoadBalancing";
	private final static Threshold STEADY_STATE_EVALUATOR = Threshold.lessThan(0.1);
	
	private final DynamicBayesianNetwork dbn;
	private final List<PcmMeasurementSpecification> pcmSpecs;
	private final NStepLoadBalancerStrategy reconfSelectionPolicy;
	
	public LoadBalancingSimulationExecutor() {
		this.dbn = LoadBalancingDBNLoader.loadOrGenerateDBN(experiment);
		this.pcmSpecs = Arrays.asList(buildResponseTimeSpec(),
								 	  buildCpuUtilizationSpecOf(CPU_SERVER_1_MONITOR),
								 	  buildCpuUtilizationSpecOf(CPU_SERVER_2_MONITOR));
//		this.reconfSelectionPolicy = new RandomizedStrategy<Action<?>>();
		this.reconfSelectionPolicy = new NStepLoadBalancerStrategy(2, pcmSpecs.get(0));
//		this.reconfSelectionPolicy = new LinearLoadBalancerStrategy(pcmSpecs.get(0));
		
		DistributionTypeModelUtil.get(BasicDistributionTypesLoader.loadRepository());
		ProbabilityDistributionFactory.get().register(new MultinomialDistributionSupplier());
	}

	@Override
	protected String getExperimentFile() {
		return SIMULIZAR_EXPERIMENT_FILE;
	}

	@Override
	public void evaluate() {
		String sampleSpaceId = SimulatedExperienceConstants.constructSampleSpaceId(SIMULATION_ID, reconfSelectionPolicy.getId());
		TotalRewardCalculation evaluator = SimulatedExperienceEvaluator.of(SIMULATION_ID, sampleSpaceId);
		LOGGER.info("***********************************************************************");
		LOGGER.info(String.format("The total Reward of policy %1s is %2s", reconfSelectionPolicy.getId(), evaluator.computeTotalReward()));
		LOGGER.info("***********************************************************************");
	}
	
	@Override
	protected ExperienceSimulator createSimulator() {
		return PcmExperienceSimulationBuilder.newBuilder()
				.makeGlobalPcmSettings()
					.withInitialExperiment(experiment)
					.andSimulatedMeasurementSpecs(Sets.newHashSet(pcmSpecs))
					.addExperienceSimulationRunner(new PcmExperienceSimulationRunner())
					.done()
				.createSimulationConfiguration()
					.withSimulationID(SIMULATION_ID)
					.withNumberOfRuns(3) //500
					.andNumberOfSimulationsPerRun(5) //100
					.andOptionalExecutionBeforeEachRun(new GlobalPcmBeforeExecutionInitialization())
					.done()
				.specifySelfAdaptiveSystemState()
				  	//.asEnvironmentalDrivenProcess(VaryingInterarrivelRateProcess.get())
				  	.asEnvironmentalDrivenProcess(VaryingInterarrivelRateProcess.get(dbn))
					.done()
				.createReconfigurationSpace()
					.addReconfigurations(getAllReconfigurations())
				  	.andReconfigurationStrategy(reconfSelectionPolicy)
				  	.done()
				.specifyRewardHandling()
				  	//.withRewardEvaluator(getSimpleRewardEvaluator())
				  	.withRewardEvaluator(getRewardEvaluator())
				  	.done()
				.build();
	}

	private RewardEvaluator getSimpleRewardEvaluator() {
		return ThresholdBasedRewardEvaluator.with(upperResponseTimeThreshold(),
			// lowerResponseTimeThreshold(),
			// cpuServer1Threshold(),
			   cpuServer2Threshold());
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
		return new HashSet<Reconfiguration<?>>(QVToReconfigurationManager.get().loadReconfigurations());
	}
	
	private PcmMeasurementSpecification buildResponseTimeSpec() {
		Monitor rtMonitor = findMonitor(RESPONSE_TIME_MONITOR);
		MeasurementSpecification rtSpec = rtMonitor.getMeasurementSpecifications().get(0);
		return PcmMeasurementSpecification.newBuilder()
				.withName(rtMonitor.getEntityName())
				.measuredAt(rtMonitor.getMeasuringPoint())
				.withMetric(rtSpec.getMetricDescription())
				.useDefaultMeasurementAggregation()
				.withOptionalSteadyStateEvaluator(STEADY_STATE_EVALUATOR)
				.build();
	}
	
	private PcmMeasurementSpecification buildCpuUtilizationSpecOf(String monitorName) {
		Monitor monitor = findMonitor(monitorName);
		MeasurementSpecification spec = monitor.getMeasurementSpecifications().get(1);
		MeasurementAggregator utilizationAggregator = new UtilizationAggregator();
        return PcmMeasurementSpecification.newBuilder()
				.withName(monitor.getEntityName())
				.measuredAt(monitor.getMeasuringPoint())
				.withMetric(spec.getMetricDescription())
				.aggregateMeasurementsBy(utilizationAggregator)
				.build();
	}

	private Monitor findMonitor(String monitorName) {
		Stream<Monitor> monitors = experiment.getInitialModel().getMonitorRepository().getMonitors().stream();
		return monitors.filter(m -> m.getEntityName().equals(monitorName))
					   .findFirst()
					   .orElseThrow(() -> new RuntimeException("There is no monitor."));
	}
	
}
