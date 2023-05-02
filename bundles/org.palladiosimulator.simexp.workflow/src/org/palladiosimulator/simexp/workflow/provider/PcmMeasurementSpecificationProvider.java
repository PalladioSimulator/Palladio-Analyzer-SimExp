package org.palladiosimulator.simexp.workflow.provider;

import java.util.stream.Stream;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.metricspec.MetricSetDescription;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.monitorrepository.Monitor;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.pcm.examples.measurements.aggregator.UtilizationAggregator;
import org.palladiosimulator.simexp.pcm.examples.performability.SystemExecutionResultTypeMeasurementAggregator;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification.MeasurementAggregator;

public class PcmMeasurementSpecificationProvider {
	private static final String RESPONSE_TIME_MONITOR = "System Response Time";
	private static final String SYSTEM_EXECUTION_RESULTTYPE = "System ExecutionResultType";

	private final Experiment experiment;

	public PcmMeasurementSpecificationProvider(Experiment experiment) {
		this.experiment = experiment;
	}

	public PcmMeasurementSpecification getSpecification(String monitorName) {
		return switch (monitorName) {
			case RESPONSE_TIME_MONITOR -> buildResponseTimeSpec();
			case SYSTEM_EXECUTION_RESULTTYPE -> buildSystemExecutionResultTypeSpec();
			default -> buildCpuUtilizationSpecOf(monitorName);
		};
	}

	private PcmMeasurementSpecification buildResponseTimeSpec() {
		Monitor rtMonitor = findMonitor(RESPONSE_TIME_MONITOR);
		MeasurementSpecification rtSpec = rtMonitor.getMeasurementSpecifications().get(0);
		return PcmMeasurementSpecification.newBuilder()
				.withName(rtMonitor.getEntityName())
				.measuredAt(rtMonitor.getMeasuringPoint())
				.withMetric(rtSpec.getMetricDescription())
				.useDefaultMeasurementAggregation()
				.withOptionalSteadyStateEvaluator(Threshold.lessThan(0.1))
				.build();
	}

	private PcmMeasurementSpecification buildSystemExecutionResultTypeSpec() {
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
