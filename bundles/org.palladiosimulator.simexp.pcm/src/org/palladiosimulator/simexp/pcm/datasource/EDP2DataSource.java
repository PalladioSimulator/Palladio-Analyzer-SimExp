package org.palladiosimulator.simexp.pcm.datasource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.measure.Measure;

import org.palladiosimulator.edp2.datastream.IDataSource;
import org.palladiosimulator.edp2.datastream.edp2source.Edp2DataTupleDataSource;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentRun;
import org.palladiosimulator.edp2.models.ExperimentData.Measurement;
import org.palladiosimulator.edp2.models.ExperimentData.RawMeasurements;
import org.palladiosimulator.measurementframework.measureprovider.IMeasureProvider;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.metricspec.MetricSetDescription;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;

public class EDP2DataSource extends DataSource {

	@Override
	public MeasurementSeriesResult getSimulatedMeasurements(ExperimentRun experimentRun) {
		return asDataSeries(filterStateMeasurements(experimentRun));
	}

	private Map<PcmMeasurementSpecification, Measurement> filterStateMeasurements(ExperimentRun experimentRun) {
		Map<PcmMeasurementSpecification, Measurement> specToMeas = new HashMap<>();
		for (Measurement each : experimentRun.getMeasurement()) {
			findSpecification(each).ifPresent(spec -> {
				if (sameMetric(each, spec)) {
					specToMeas.put(spec, each);
				}
			});
		}

		// TODO exception handling
		if (specToMeas.isEmpty()) {
			throw new RuntimeException("");
		}
		return specToMeas;
	}

	private Optional<PcmMeasurementSpecification> findSpecification(Measurement measurement) {
		return getMeasurementSpecs().stream()
				.filter(spec -> spec.hasMeasuringPoint(measurement.getMeasuringType().getMeasuringPoint())).findFirst();
	}

	private MeasurementSeriesResult asDataSeries(Map<PcmMeasurementSpecification, Measurement> measurements) {
		MeasurementSeriesResult result = new MeasurementSeriesResult();
		for (PcmMeasurementSpecification each : measurements.keySet()) {
			Measurement measure = measurements.get(each);
			MetricDescription desc = each.getMetricDescription();
			result.addMeasurementSeries(each, getMeasurementSeries(measure, desc));
		}
		return result;
	}

	private boolean sameMetric(Measurement measurement, PcmMeasurementSpecification spec) {
		MetricDescription first = spec.getMetricDescription();
		MetricDescription second = measurement.getMeasuringType().getMetric();
		if (second instanceof MetricSetDescription) {
			return containsMetric(first, (MetricSetDescription) second);
		}

		return sameMetric(first, second);
	}

	private boolean containsMetric(MetricDescription first, MetricSetDescription second) {
		return second.getSubsumedMetrics().stream().anyMatch(m -> sameMetric(first, m));
	}

	private boolean sameMetric(MetricDescription first, MetricDescription second) {
		return first.getId().equals(second.getId());
	}

	private List<Pair<Number, Double>> getMeasurementSeries(Measurement measurement, MetricDescription metricDesc) {
		List<Pair<Number, Double>> measurementSeries = new ArrayList<>();

		Iterator<IMeasureProvider> iterator = getIterator(measurement);
		while (iterator.hasNext()) {
			IMeasureProvider provider = iterator.next();
			Measure<?, ?> stateQuantity = provider.getMeasureForMetric(metricDesc);
			Measure<?, ?> timeInstant = provider.getMeasureForMetric(MetricDescriptionConstants.POINT_IN_TIME_METRIC);
			if (stateQuantity.getValue() instanceof Number) {
				measurementSeries.add(Pair.of((Number) stateQuantity.getValue(), (Double) timeInstant.getValue()));
			}
		}

		return measurementSeries;
	}

	private Iterator<IMeasureProvider> getIterator(Measurement measurement) {
		RawMeasurements rawMeasurements = measurement.getMeasurementRanges().get(0).getRawMeasurements();
		IDataSource dataSource = new Edp2DataTupleDataSource(rawMeasurements);
		return dataSource.getDataStream().iterator();
	}
}
