package org.palladiosimulator.simexp.pcm.examples.measurements.aggregator;

import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.pcm.datasource.MeasurementSeriesResult.MeasurementSeries;
import org.palladiosimulator.simexp.pcm.datasource.MeasurementSeriesResult.MeasurementValue;
import org.palladiosimulator.simexp.pcm.datasource.MeasurementSeriesResult.PointInTime;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;

public class UtilizationAggregator implements PcmMeasurementSpecification.MeasurementAggregator {
    
    private static final Logger LOGGER = Logger.getLogger(UtilizationAggregator.class.getName());

    @Override
    public double aggregate(MeasurementSeries series) {
        double utilization = 0;

        List<Pair<PointInTime, MeasurementValue>> measurements = series.asList();
        for (int i = 0; i < measurements.size() - 1; i++) {
            Pair<PointInTime, MeasurementValue> current = measurements.get(i);
            Pair<PointInTime, MeasurementValue> next = measurements.get(i + 1);
            if (isActive(current, next) || isIdle(current, next)) {
                utilization += getTimeInstant(next) - getTimeInstant(current);
            }
        }

        return computeUtilization(utilization, getTotalTime(measurements));
    }

    private Double getTotalTime(List<Pair<PointInTime, MeasurementValue>> measurements) {
        int last = measurements.size() - 1;
        return getTimeInstant(measurements.get(last));
    }

    private double computeUtilization(double utilization, Double totalTime) {
        return utilization / totalTime;
    }

    private boolean isActive(Pair<PointInTime, MeasurementValue> current, Pair<PointInTime, MeasurementValue> next) {
        int resourceStateCurr = getResourceState(current);
        int resourceStateNext = getResourceState(next);
        boolean isActive = resourceStateCurr > 0 && resourceStateNext > 0;
        return isActive;
    }

    private boolean isIdle(Pair<PointInTime, MeasurementValue> current, Pair<PointInTime, MeasurementValue> next) {
        return getResourceState(current) > 0 && getResourceState(next) == 0;
    }

    private Integer getResourceState(Pair<PointInTime, MeasurementValue> measurement) {
        MeasurementValue measurementValue = measurement.getSecond();
        Object value = measurementValue.getValue();
        if (value instanceof Double) {
            return ((Double) value).intValue();
        }
        LOGGER.debug("Failed to read measurement value. Could not convert to type 'double'");
        return null;
    }

    private Double getTimeInstant(Pair<PointInTime, MeasurementValue> measurement) {
        PointInTime pointInTime = measurement.getFirst();
        return Double.valueOf(pointInTime.getPointInTime());
    }

}
