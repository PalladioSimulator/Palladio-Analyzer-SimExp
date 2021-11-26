package org.palladiosimulator.simexp.pcm.datasource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.palladiosimulator.simexp.core.util.Pair;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;

public class MeasurementSeriesResult {

    public static abstract class MeasurementValue {
        public abstract Object getValue();
    }

    public static class StringMeasurementValue extends MeasurementValue {

        private final String value;

        public StringMeasurementValue(String value) {
            this.value = value;
        }
        
        @Override
        public String getValue() {
            return value;
        }
    }

    public static class NumberMeasurementValue<T extends Number> extends MeasurementValue {

        private final T value;
        private final Class<T> valueClazz;

        public NumberMeasurementValue(T value, Class<T> clazz) {
            this.value = value;
            this.valueClazz = clazz;
        }

        @Override
        public T getValue() {
            return value;
        }

        public Class<T> getValueClazz() {
            return valueClazz;
        }
    }
    
    // FIXME: should be replaced by a suitable Java standard library class
    public static class PointInTime {
        
        private final double pointInTime;

        public PointInTime(double pointInTime) {
            this.pointInTime = pointInTime;
        }

        public double getPointInTime() {
            return pointInTime;
        }
    }

    public static class MeasurementSeries {

        private final List<Pair<PointInTime, MeasurementValue>> measurements;

        public MeasurementSeries(List<Pair<PointInTime, MeasurementValue>> measurements) {
            this.measurements = measurements;
        }

        public Stream<Pair<PointInTime, MeasurementValue>> asStream() {
            /*Pair<Number, MeasurementValue>  p = measurements.get(0);
            MeasurementValue second = p.getSecond();
            if (second instanceof NumberMeasurementValue) {
                @SuppressWarnings("unchecked")
                NumberMeasurementValue<Number> numberMeasurementValue = (NumberMeasurementValue<Number>) second;
                Number n = numberMeasurementValue.getValue();
                
                Class<?> valueClazz = numberMeasurementValue.getValueClazz();
                if (valueClazz.equals(Double.class)) {
                    Double d = (Double) numberMeasurementValue.getValue();
                }
            }*/
            return measurements.stream();
        }

        public List<Pair<PointInTime, MeasurementValue>> asList() {
            return measurements;
        }

        public Stream<MeasurementValue> asStreamOfValues() {
            return asListOfValues().stream();
        }

        public List<MeasurementValue> asListOfValues() {
            return measurements.stream()
                .map(each -> each.getSecond())
                .collect(Collectors.toList());
        }

    }

    private final Map<PcmMeasurementSpecification, MeasurementSeries> results;

    public MeasurementSeriesResult() {
        this.results = new HashMap<>();
    }

    public void addMeasurementSeries(PcmMeasurementSpecification spec, List<Pair<PointInTime, MeasurementValue>> measurements) {
        results.put(spec, new MeasurementSeries(measurements));
    }

    public Optional<MeasurementSeries> getMeasurementsSeries(PcmMeasurementSpecification spec) {
        return Optional.ofNullable(results.get(spec));
    }
}
