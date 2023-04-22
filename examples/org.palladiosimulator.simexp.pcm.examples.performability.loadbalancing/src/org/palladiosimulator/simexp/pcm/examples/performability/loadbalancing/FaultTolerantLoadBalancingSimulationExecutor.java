package org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.metricspec.MetricSetDescription;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.monitorrepository.Monitor;
import org.palladiosimulator.pcm.query.RepositoryModelLookup;
import org.palladiosimulator.pcm.query.ResourceEnvironmentModelLookup;
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
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.examples.measurements.aggregator.UtilizationAggregator;
import org.palladiosimulator.simexp.pcm.examples.performability.NodeRecoveryStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.PerformabilityRewardEvaluation;
import org.palladiosimulator.simexp.pcm.examples.performability.PerformabilityStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.PerformabilityStrategyConfiguration;
import org.palladiosimulator.simexp.pcm.examples.performability.ReconfigurationPlanningStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.RepositoryModelUpdater;
import org.palladiosimulator.simexp.pcm.examples.performability.SystemExecutionResultTypeMeasurementAggregator;
import org.palladiosimulator.simexp.pcm.init.GlobalPcmBeforeExecutionInitialization;
import org.palladiosimulator.simexp.pcm.process.PerformabilityPcmExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification.MeasurementAggregator;
import org.palladiosimulator.simexp.pcm.util.SimulationParameterConfiguration;

import com.google.common.collect.Sets;

import tools.mdsd.probdist.api.apache.supplier.MultinomialDistributionSupplier;
import tools.mdsd.probdist.api.apache.util.IProbabilityDistributionRepositoryLookup;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;
import tools.mdsd.probdist.api.factory.IProbabilityDistributionRegistry;
import tools.mdsd.probdist.api.parser.ParameterParser;

public class FaultTolerantLoadBalancingSimulationExecutor extends PcmExperienceSimulationExecutor {
    
    private static final Threshold LOWER_THRESHOLD_RT = Threshold.greaterThanOrEqualTo(0.1);
    private static final Threshold UPPER_THRESHOLD_RT = Threshold.lessThanOrEqualTo(0.4);
	
	private final static double THRESHOLD_UTIL_1 = 0.7;
	private final static double THRESHOLD_UTIL_2 = 0.5;
	private final static String RESPONSE_TIME_MONITOR = "System Response Time";
	private final static String CPU_SERVER_1_MONITOR = "cpuServer1";
	private final static String CPU_SERVER_2_MONITOR = "cpuServer2";
    private final static String SYSTEM_EXECUTION_RESULTTYPE = "System ExecutionResultType";
	private final static String SIMULATION_ID = "LoadBalancing";
	private final static Threshold STEADY_STATE_EVALUATOR = Threshold.lessThan(0.1);
	
    private static final String SERVER_FAILURE_TEMPLATE_ID = "_VtIJEPtrEeuPUtFH1XJrHw";
    private static final String LOAD_BALANCER_ID = "_NvLi8AEmEeS7FKokKTKFow";
	
	private final DynamicBayesianNetwork dbn;
	private final List<PcmMeasurementSpecification> pcmSpecs;
	private final ReconfigurationStrategy<QVToReconfiguration> reconfSelectionPolicy;
	
    private final PcmMeasurementSpecification responseTimeMeasurementSpec;
    private final PcmMeasurementSpecification systemResultExectutionTypeTimeMeasurementSpec;
    
	private final NodeRecoveryStrategy nodeRecoveryStrategy;
	private PerformabilityStrategyConfiguration strategyConfiguration;
	private final ReconfigurationPlanningStrategy reconfigurationPlanningStrategy;
		
	private FaultTolerantLoadBalancingSimulationExecutor(Experiment experiment, DynamicBayesianNetwork dbn, 
			IProbabilityDistributionRegistry probabilityDistributionRegistry, 
			IProbabilityDistributionFactory probabilityDistributionFactory, ParameterParser parameterParser, 
			IProbabilityDistributionRepositoryLookup probDistRepoLookup, SimulationParameterConfiguration simulationParameters,
			List<PcmMeasurementSpecification> pcmSpecs) {
		super(experiment, simulationParameters);
		this.dbn = dbn;
		this.pcmSpecs = pcmSpecs;
		
		/*
		this.pcmSpecs = Arrays.asList(buildResponseTimeSpec(),
								 	  buildCpuUtilizationSpecOf(CPU_SERVER_1_MONITOR),
								 	  buildCpuUtilizationSpecOf(CPU_SERVER_2_MONITOR),
								 	 buildSystemExecutionResultTypeSpec(SYSTEM_EXECUTION_RESULTTYPE));
								 	 */
		this.strategyConfiguration = new PerformabilityStrategyConfiguration(SERVER_FAILURE_TEMPLATE_ID, LOAD_BALANCER_ID);
		
		this.responseTimeMeasurementSpec = pcmSpecs.get(0);
        this.systemResultExectutionTypeTimeMeasurementSpec = pcmSpecs.get(3);
		this.nodeRecoveryStrategy = new FaultTolerantScalingNodeFailureRecoveryStrategy(strategyConfiguration, new RepositoryModelLookup()
		        , new ResourceEnvironmentModelLookup(), new RepositoryModelUpdater());

		// configure the different planning strategies that shall be investigated by accordingly (un)comment the required strategy definition
		this.reconfigurationPlanningStrategy = new LoadBalancingEmptyReconfigurationPlanningStrategy(responseTimeMeasurementSpec, strategyConfiguration, nodeRecoveryStrategy);
//        this.reconfigurationPlanningStrategy = new LoadBalancingScalingPlanningStrategy(responseTimeMeasurementSpec, strategyConfiguration
//                , nodeRecoveryStrategy , LOWER_THRESHOLD_RT, UPPER_THRESHOLD_RT);
//        this.reconfigurationPlanningStrategy = new FaultTolerantScalingPlanningStrategy(responseTimeMeasurementSpec, strategyConfiguration
//                , nodeRecoveryStrategy, LOWER_THRESHOLD_RT, UPPER_THRESHOLD_RT);

        this.reconfSelectionPolicy = new PerformabilityStrategy(responseTimeMeasurementSpec, strategyConfiguration, reconfigurationPlanningStrategy);
		
		probabilityDistributionRegistry.register(new MultinomialDistributionSupplier(parameterParser, probDistRepoLookup));
	}
	
	public static final class FaultTolerantLoadBalancingSimulationExecutorFactory {
	    public FaultTolerantLoadBalancingSimulationExecutor create(Experiment experiment, DynamicBayesianNetwork dbn, 
	    		IProbabilityDistributionRegistry probabilityDistributionRegistry, IProbabilityDistributionFactory probabilityDistributionFactory, 
	    		ParameterParser parameterParser, IProbabilityDistributionRepositoryLookup probDistRepoLookup, SimulationParameterConfiguration simulationParameters,
	    		List<PcmMeasurementSpecification> pcmSpecs) {
	        return new FaultTolerantLoadBalancingSimulationExecutor(experiment, dbn, probabilityDistributionRegistry, probabilityDistributionFactory, 
	        		parameterParser, probDistRepoLookup, simulationParameters, pcmSpecs);
	    }
	}

	@Override
	public void evaluate() {
		String sampleSpaceId = SimulatedExperienceConstants.constructSampleSpaceId(SIMULATION_ID, reconfSelectionPolicy.getId());
		TotalRewardCalculation evaluator = new PerformabilityEvaluator(SIMULATION_ID, sampleSpaceId);
		LOGGER.info("***********************************************************************");
		double computeTotalReward = evaluator.computeTotalReward();
        LOGGER.info(String.format("The total Reward of policy %1s is %2s", reconfSelectionPolicy.getId(), computeTotalReward));
		LOGGER.info("***********************************************************************");
	}
	
	@Override
	protected ExperienceSimulator createSimulator() {
		return PcmExperienceSimulationBuilder.newBuilder()
				.makeGlobalPcmSettings()
					.withInitialExperiment(experiment)
					.andSimulatedMeasurementSpecs(Sets.newHashSet(pcmSpecs))
					.addExperienceSimulationRunner(new PerformabilityPcmExperienceSimulationRunner())
					.done()
				.createSimulationConfiguration()
					.withSimulationID(simulationParameters.getSimulationID())
					.withNumberOfRuns(simulationParameters.getNumberOfRuns()) //500
					.andNumberOfSimulationsPerRun(simulationParameters.getNumberOfSimulationsPerRun()) //100
					.andOptionalExecutionBeforeEachRun(new GlobalPcmBeforeExecutionInitialization())
					.done()
				.specifySelfAdaptiveSystemState()
				  	.asEnvironmentalDrivenProcess(FaultTolerantVaryingInterarrivelRateProcess.get(dbn))
					.done()
				.createReconfigurationSpace()
					.addReconfigurations(getAllReconfigurations())
				  	.andReconfigurationStrategy(reconfSelectionPolicy)
				  	.done()
				.specifyRewardHandling()
				  	.withRewardEvaluator(getPerformabilityRewardEvaluator())
				  	.done()
				.build();
	}

    private RewardEvaluator getPerformabilityRewardEvaluator() {
        return new PerformabilityRewardEvaluation(responseTimeMeasurementSpec, systemResultExectutionTypeTimeMeasurementSpec
                , lowerResponseTimeThreshold(), upperResponseTimeThreshold());
    }

	private Pair<SimulatedMeasurementSpecification, Threshold> upperResponseTimeThreshold() {
		return Pair.of(pcmSpecs.get(0), Threshold.lessThanOrEqualTo(UPPER_THRESHOLD_RT.getValue()));
	}
	
	private Pair<SimulatedMeasurementSpecification, Threshold> lowerResponseTimeThreshold() {
		return Pair.of(pcmSpecs.get(0), Threshold.greaterThanOrEqualTo(LOWER_THRESHOLD_RT.getValue()));
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
	
	private PcmMeasurementSpecification buildSystemExecutionResultTypeSpec(String systemExecutionResulttype) {
        Monitor monitor = findMonitor(SYSTEM_EXECUTION_RESULTTYPE);
        EList<MeasurementSpecification> measurementSpecifications = monitor.getMeasurementSpecifications();
        // this is a MetricDescriptionSet -> thus you need to add the contained BaseMetric
        MeasurementSpecification measurementSpec = measurementSpecifications.get(0);
        MeasurementAggregator systemExecResultTypeAggregtator = new SystemExecutionResultTypeMeasurementAggregator();
        MetricDescription metricDescription = measurementSpec.getMetricDescription();
        EList<MetricDescription> subsumedMetrics = ((MetricSetDescription) metricDescription).getSubsumedMetrics();
        MetricDescription subsumedTextualBaseMetricDescription = subsumedMetrics.get(1);
        return PcmMeasurementSpecification.newBuilder()
            .withName(monitor.getEntityName())
            .measuredAt(monitor.getMeasuringPoint())
            .withMetric(subsumedTextualBaseMetricDescription)
            .aggregateMeasurementsBy(systemExecResultTypeAggregtator)
            .build();
    }

	private Monitor findMonitor(String monitorName) {
		Stream<Monitor> monitors = experiment.getInitialModel().getMonitorRepository().getMonitors().stream();
		return monitors.filter(m -> m.getEntityName().equals(monitorName))
					   .findFirst()
					   .orElseThrow(() -> new RuntimeException("There is no monitor."));
	}
	
}
