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
import org.palladiosimulator.metricspec.Identifier;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.pcm.datasource.MeasurementSeriesResult.MeasurementValue;
import org.palladiosimulator.simexp.pcm.datasource.MeasurementSeriesResult.NumberMeasurementValue;
import org.palladiosimulator.simexp.pcm.datasource.MeasurementSeriesResult.PointInTime;
import org.palladiosimulator.simexp.pcm.datasource.MeasurementSeriesResult.StringMeasurementValue;
import org.palladiosimulator.simexp.pcm.state.InitialPcmStateCreator;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;

public class EDP2DataSource<A> extends DataSource {

    private final InitialPcmStateCreator<A> initialStateCreator;

    public EDP2DataSource(InitialPcmStateCreator<A> initialStateCreator) {
        this.initialStateCreator = initialStateCreator;
    }

    @Override
    public MeasurementSeriesResult getSimulatedMeasurements(List<ExperimentRun> experimentRuns) {
        StateMeasurementFilter filter = new StateMeasurementFilter(initialStateCreator);
        Map<PcmMeasurementSpecification, Measurement> filterStateMeasurements = filter
            .filterStateMeasurements(experimentRuns);
        return asDataSeries(filterStateMeasurements);
    }

    private MeasurementSeriesResult asDataSeries(Map<PcmMeasurementSpecification, Measurement> measurements) {
        MeasurementSeriesResult result = new MeasurementSeriesResult();
        for (PcmMeasurementSpecification each : measurements.keySet()) {
            Measurement measure = measurements.get(each);
            MetricDescription desc = each.getMetricDescription();
            List<Pair<PointInTime, MeasurementValue>> measurementSeries = getMeasurementSeries(measure, desc);
            result.addMeasurementSeries(each, measurementSeries);
        }
        return result;
    }

    private List<Pair<PointInTime, MeasurementValue>> getMeasurementSeries(Measurement measurement,
            MetricDescription metricDesc) {
        List<Pair<PointInTime, MeasurementValue>> measurementSeries = new ArrayList<>();

        Iterator<IMeasureProvider> iterator = getIterator(measurement);
        while (iterator.hasNext()) {
            IMeasureProvider provider = iterator.next();
            Measure<?, ?> stateQuantity = provider.getMeasureForMetric(metricDesc);
            Measure<?, ?> timeInstant = provider.getMeasureForMetric(MetricDescriptionConstants.POINT_IN_TIME_METRIC);
            // point in time the measurement of a specific metric was taken
            Double timeInstantValue = (Double) timeInstant.getValue();
            PointInTime pointInTime = new MeasurementSeriesResult.PointInTime(timeInstantValue);
            // measured value of specific metric
            Object stateQuantityValue = stateQuantity.getValue();

            if (stateQuantityValue instanceof Identifier) {
                Identifier stateQuantityValueAsIdentifier = (Identifier) stateQuantityValue;
                String stateQuantityAsStringValue = stateQuantityValueAsIdentifier.getLiteral();
                StringMeasurementValue measurementValueAsString = new MeasurementSeriesResult.StringMeasurementValue(
                        stateQuantityAsStringValue);
                Pair<PointInTime, MeasurementValue> measurementValuePair = Pair.of(pointInTime,
                        measurementValueAsString);
                measurementSeries.add(measurementValuePair);
            }
            if (stateQuantityValue instanceof Number) {
                Number number = (Number) stateQuantityValue;
                if (number instanceof Double) {
                    Double stateQuantityAsDoubleValue = (Double) stateQuantityValue;
                    NumberMeasurementValue<Double> measurementValueAsDouble = new MeasurementSeriesResult.NumberMeasurementValue<Double>(
                            stateQuantityAsDoubleValue, Double.class);
                    Pair<PointInTime, MeasurementValue> measurementValuePair = Pair.of(pointInTime,
                            measurementValueAsDouble);
                    measurementSeries.add(measurementValuePair);
                }
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
