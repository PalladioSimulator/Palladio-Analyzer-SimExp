package org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.monitorrepository.Monitor;
import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.evaluation.PerformabilityEvaluator;
import org.palladiosimulator.simexp.core.evaluation.TotalRewardCalculation;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.builder.PcmExperienceSimulationBuilder;
import org.palladiosimulator.simexp.pcm.datasource.MeasurementSeriesResult.MeasurementSeries;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.examples.performability.PerformabilityRewardEvaluation;
import org.palladiosimulator.simexp.pcm.examples.performability.PerformabilityStrategy;
import org.palladiosimulator.simexp.pcm.init.GlobalPcmBeforeExecutionInitialization;
import org.palladiosimulator.simexp.pcm.process.PerformabilityPcmExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification.MeasurementAggregator;

import com.google.common.collect.Sets;

import tools.mdsd.probdist.api.apache.supplier.MultinomialDistributionSupplier;
import tools.mdsd.probdist.api.apache.util.DistributionTypeModelUtil;
import tools.mdsd.probdist.api.factory.ProbabilityDistributionFactory;
import tools.mdsd.probdist.model.basic.loader.BasicDistributionTypesLoader;

public class FaultTolerantLoadBalancingSimulationExecutor extends PcmExperienceSimulationExecutor {
    
	public final static double UPPER_THRESHOLD_RT = 2.0;
	public final static double LOWER_THRESHOLD_RT = 1.0;
	
	private final static String EXPERIMENT_FILE = "/org.palladiosimulator.simexp.pcm.examples.loadbalancer.faulttolerant/experiments/simexp.experiments";
	private final static double THRESHOLD_UTIL_1 = 0.7;
	private final static double THRESHOLD_UTIL_2 = 0.5;
	private final static String RESPONSE_TIME_MONITOR = "System Response Time";
	private final static String CPU_SERVER_1_MONITOR = "cpuServer1";
	private final static String CPU_SERVER_2_MONITOR = "cpuServer2";
	private final static String SIMULATION_ID = "LoadBalancing";
	private final static Threshold STEADY_STATE_EVALUATOR = Threshold.lessThan(0.1);
	
	private final DynamicBayesianNetwork dbn;
	private final List<PcmMeasurementSpecification> pcmSpecs;
	private final ReconfigurationStrategy<QVToReconfiguration> reconfSelectionPolicy;
	
	public FaultTolerantLoadBalancingSimulationExecutor() {
		this.dbn = FaultTolerantLoadBalancingDBNLoader.loadOrGenerateDBN(experiment);
		this.pcmSpecs = Arrays.asList(buildResponseTimeSpec(),
								 	  buildCpuUtilizationSpecOf(CPU_SERVER_1_MONITOR),
								 	  buildCpuUtilizationSpecOf(CPU_SERVER_2_MONITOR));
//		this.reconfSelectionPolicy = new RandomizedStrategy<Action<?>>();
		this.reconfSelectionPolicy = new PerformabilityStrategy(pcmSpecs.get(0));
//		this.reconfSelectionPolicy = new NStepLoadBalancerStrategy(2, pcmSpecs.get(0));
//		this.reconfSelectionPolicy = new LinearLoadBalancerStrategy(pcmSpecs.get(0));
		
		DistributionTypeModelUtil.get(BasicDistributionTypesLoader.loadRepository());
		ProbabilityDistributionFactory.get().register(new MultinomialDistributionSupplier());
	}

	@Override
	protected String getExperimentFile() {
		return EXPERIMENT_FILE;
	}

	@Override
	public void evaluate() {
		String sampleSpaceId = SimulatedExperienceConstants.constructSampleSpaceId(SIMULATION_ID, reconfSelectionPolicy.getId());
		TotalRewardCalculation evaluator = new PerformabilityEvaluator(SIMULATION_ID, sampleSpaceId);
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
//					.addExperienceSimulationRunner(new PcmExperienceSimulationRunner())
					.addExperienceSimulationRunner(new PerformabilityPcmExperienceSimulationRunner())
					.done()
				.createSimulationConfiguration()
					.withSimulationID(SIMULATION_ID)
					.withNumberOfRuns(3) //500
					.andNumberOfSimulationsPerRun(5) //100
					.andOptionalExecutionBeforeEachRun(new GlobalPcmBeforeExecutionInitialization())
					.done()
				.specifySelfAdaptiveSystemState()
				  	//.asEnvironmentalDrivenProcess(VaryingInterarrivelRateProcess.get())
				  	.asEnvironmentalDrivenProcess(FaultTolerantVaryingInterarrivelRateProcess.get(dbn))
					.done()
				.createReconfigurationSpace()
					.addReconfigurations(getAllReconfigurations())
				  	.andReconfigurationStrategy(reconfSelectionPolicy)
				  	.done()
				.specifyRewardHandling()
				  	//.withRewardEvaluator(getSimpleRewardEvaluator())
				  	.withRewardEvaluator(getPerformabilityRewardEvaluator())
				  	.done()
				.build();
	}

    private RewardEvaluator getPerformabilityRewardEvaluator() {
        PcmMeasurementSpecification responseTimeMeasurementSpec = pcmSpecs.get(0);
        return new PerformabilityRewardEvaluation(responseTimeMeasurementSpec);
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
		return PcmMeasurementSpecification.newBuilder()
				.withName(monitor.getEntityName())
				.measuredAt(monitor.getMeasuringPoint())
				.withMetric(spec.getMetricDescription())
				.aggregateMeasurementsBy(getUtilizationAggregator())
				.build();
	}

	private Monitor findMonitor(String monitorName) {
		Stream<Monitor> monitors = experiment.getInitialModel().getMonitorRepository().getMonitors().stream();
		return monitors.filter(m -> m.getEntityName().equals(monitorName))
					   .findFirst()
					   .orElseThrow(() -> new RuntimeException("There is no monitor."));
	}
	
	private MeasurementAggregator getUtilizationAggregator() {
		return new PcmMeasurementSpecification.MeasurementAggregator() {
			
			@Override
			public double aggregate(MeasurementSeries series) {
				double utilization = 0;
				
				List<Pair<Number, Double>> measurements = series.asList();
				for (int i = 0; i < measurements.size() - 1; i++) {
					Pair<Number, Double> current = measurements.get(i);
					Pair<Number, Double> next = measurements.get(i + 1);
					if (isActive(current, next) || isIdle(current, next)) {
						utilization += getTimeInstant(next) - getTimeInstant(current);
					}
				}
				
				return computeUtilization(utilization, getTotalTime(measurements));
			}

			private Double getTotalTime(List<Pair<Number, Double>> measurements) {
				int last = measurements.size() - 1;
				return getTimeInstant(measurements.get(last));
			}

			private double computeUtilization(double utilization, Double totalTime) {
				return utilization / totalTime;
			}

			private boolean isActive(Pair<Number, Double> current, Pair<Number, Double> next) {
				return getResourceState(current) > 0 && getResourceState(next) > 0;
			}

			private boolean isIdle(Pair<Number, Double> current, Pair<Number, Double> next) {
				return getResourceState(current) > 0 && getResourceState(next) == 0;
			}
			
			private Integer getResourceState(Pair<Number, Double> measurement) {
				return measurement.getFirst().intValue();
			}
			
			private Double getTimeInstant(Pair<Number, Double> measurement) {
				return measurement.getSecond();
			}
			
		};
	}

}