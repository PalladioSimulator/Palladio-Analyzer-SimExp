package org.palladiosimulator.simexp.pcm.datasource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.measure.Measure;

import org.palladiosimulator.edp2.datastream.IDataSource;
import org.palladiosimulator.edp2.datastream.edp2source.Edp2DataTupleDataSource;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentRun;
import org.palladiosimulator.edp2.models.ExperimentData.Measurement;
import org.palladiosimulator.edp2.models.ExperimentData.RawMeasurements;
import org.palladiosimulator.measurementframework.measureprovider.IMeasureProvider;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;

public class EDP2DataSource extends DataSource {

    @Override
    public MeasurementSeriesResult getSimulatedMeasurements(List<ExperimentRun> experimentRuns) {
        StateMeasurementFilter filter = new StateMeasurementFilter();
        Map<PcmMeasurementSpecification, Measurement> filterStateMeasurements = filter
            .filterStateMeasurements(experimentRuns);
        return asDataSeries(filterStateMeasurements);
    }

    private MeasurementSeriesResult asDataSeries(Map<PcmMeasurementSpecification, Measurement> measurements) {
        MeasurementSeriesResult result = new MeasurementSeriesResult();
        for (PcmMeasurementSpecification each : measurements.keySet()) {
            Measurement measure = measurements.get(each);
            MetricDescription desc = each.getMetricDescription();
            List<Pair<Number, Double>> measurementSeries = getMeasurementSeries(measure, desc);
            result.addMeasurementSeries(each, measurementSeries);
        }
        return result;
    }

    private List<Pair<Number, Double>> getMeasurementSeries(Measurement measurement, MetricDescription metricDesc) {
        List<Pair<Number, Double>> measurementSeries = new ArrayList<>();
        Iterator<IMeasureProvider> iterator = getIterator(measurement);
        while (iterator.hasNext()) {
            IMeasureProvider provider = iterator.next();
            Measure<?, ?> stateQuantity = provider.getMeasureForMetric(metricDesc);
            Measure<?, ?> timeInstant = provider.getMeasureForMetric(MetricDescriptionConstants.POINT_IN_TIME_METRIC);
            Object stateQuantityValue = stateQuantity.getValue();
            if (stateQuantityValue instanceof Number) {
                Number number = (Number) stateQuantityValue;
                measurementSeries.add(Pair.of(number, (Double) timeInstant.getValue()));
            }
        }
        return measurementSeries;
    }

    private Iterator<IMeasureProvider> getIterator(Measurement measurement) {
        RawMeasurements rawMeasurements = measurement.getMeasurementRanges()
            .get(0)
            .getRawMeasurements();
        IDataSource dataSource = new Edp2DataTupleDataSource(rawMeasurements);
        return dataSource.getDataStream()
            .iterator();
    }
}
