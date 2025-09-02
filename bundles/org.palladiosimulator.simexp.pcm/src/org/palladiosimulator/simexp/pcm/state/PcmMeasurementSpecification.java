package org.palladiosimulator.simexp.pcm.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.log4j.Logger;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.statistics.StatisticalQuantities;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.pcm.datasource.MeasurementSeriesResult.MeasurementSeries;
import org.palladiosimulator.simexp.pcm.datasource.MeasurementSeriesResult.MeasurementValue;

public class PcmMeasurementSpecification extends SimulatedMeasurementSpecification {

    public interface MeasurementAggregator {

        public static MeasurementAggregator getDefault() {
            return new DefaultMeasurementAggregator();
        }

        public double aggregate(MeasurementSeries measurements);
    }

    private static final class DefaultMeasurementAggregator implements MeasurementAggregator {

        private static final Logger LOGGER = Logger.getLogger(DefaultMeasurementAggregator.class.getName());

        @Override
        public double aggregate(MeasurementSeries measurements) {
            List<MeasurementValue> measurementsValues = measurements.asListOfValues();

            List<Number> measurementsAsNumbers = new ArrayList<>();
            for (MeasurementValue measurementValue : measurementsValues) {
                Object value = measurementValue.getValue();
                if (value instanceof Number) {
                    measurementsAsNumbers.add((Number) value);
                }
            }

            if (measurementsAsNumbers.isEmpty()) {
                LOGGER.error("No measurements available from simulation.");
                return Double.NaN;
            }

//            LOGGER.debug("Taken measurements from simulation:");
//            for (Number number : measurementsAsNumbers) {
//                LOGGER.debug(String.format("measurement = %s", number.toString()));
//            }

            double aggregatedMeasurements = StatisticalQuantities.withNumbers(measurementsAsNumbers)
                .mean();
            LOGGER.info(String.format("Aggregated measurements = %s", aggregatedMeasurements));
            return aggregatedMeasurements;
        }
    }

    public static class PcmMeasurementSpecBuilder {

        private String name = null;
        private MeasuringPoint measuringPoint = null;
        private MetricDescription metricDescription = null;
        private MeasurementAggregator aggregator = null;
        private Optional<Threshold> steadyStateEvaluator = Optional.empty();

        public PcmMeasurementSpecBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public PcmMeasurementSpecBuilder measuredAt(MeasuringPoint measPoint) {
            this.measuringPoint = measPoint;
            return this;
        }

        public PcmMeasurementSpecBuilder withMetric(MetricDescription metricDesc) {
            this.metricDescription = metricDesc;
            return this;
        }

        public PcmMeasurementSpecBuilder useDefaultMeasurementAggregation() {
            this.aggregator = MeasurementAggregator.getDefault();
            return this;
        }

        public PcmMeasurementSpecBuilder aggregateMeasurementsBy(MeasurementAggregator aggregator) {
            this.aggregator = aggregator;
            return this;
        }

        public PcmMeasurementSpecBuilder withOptionalSteadyStateEvaluator(Threshold evaluator) {
            this.steadyStateEvaluator = Optional.of(evaluator);
            return this;
        }

        public PcmMeasurementSpecification build() {
            // TODO Exception handling
            Objects.requireNonNull(name, "");
            Objects.requireNonNull(measuringPoint, "");
            Objects.requireNonNull(metricDescription, "");
            Objects.requireNonNull(aggregator, "");

            return new PcmMeasurementSpecification(deriveUniqueId(measuringPoint, metricDescription), name,
                    measuringPoint, metricDescription, aggregator, steadyStateEvaluator);
        }

    }

    private final MeasuringPoint measuringPoint;
    private final MetricDescription metricDescription;
    private final MeasurementAggregator aggregator;
    private final Optional<Threshold> steadyStateEvaluator;

    private PcmMeasurementSpecification(String id, String name, MeasuringPoint measuringPoint,
            MetricDescription metricDescription, MeasurementAggregator aggregator,
            Optional<Threshold> steadyStateEvaluator) {
        super(id, name);
        this.measuringPoint = measuringPoint;
        this.metricDescription = metricDescription;
        this.aggregator = aggregator;
        this.steadyStateEvaluator = steadyStateEvaluator;
    }

    public static PcmMeasurementSpecBuilder newBuilder() {
        return new PcmMeasurementSpecBuilder();
    }

    public boolean hasMeasuringPoint(MeasuringPoint measuringPoint) {
        return this.measuringPoint.getStringRepresentation()
            .equals(measuringPoint.getStringRepresentation());
    }

    public boolean hasMetricDescription(MetricDescription metricDescription) {
        return this.metricDescription.getId()
            .equals(metricDescription.getId());
    }

    public MeasuringPoint getMeasuringPoint() {
        return measuringPoint;
    }

    public MetricDescription getMetricDescription() {
        return metricDescription;
    }

    public Optional<Threshold> getSteadyStateEvaluator() {
        return steadyStateEvaluator;
    }

    public double computeQuantity(MeasurementSeries measurements) {
        return aggregator.aggregate(measurements);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 23) //
            .appendSuper(super.hashCode())
            .append(measuringPoint.getStringRepresentation())
            .append(metricDescription.getId())
            .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        PcmMeasurementSpecification rhs = (PcmMeasurementSpecification) obj;
        return new EqualsBuilder() //
            .appendSuper(super.equals(obj))
            .append(measuringPoint.getStringRepresentation(), rhs.measuringPoint.getStringRepresentation())
            .append(metricDescription.getId(), rhs.metricDescription.getId())
            .isEquals();
    }

    @Override
    public String toString() {
        return deriveUniqueId(this.measuringPoint, this.metricDescription);
    }

    private static String deriveUniqueId(MeasuringPoint measuringPoint, MetricDescription desc) {
        return String.format("%1s_%2s", measuringPoint.getStringRepresentation(), desc.getName());
    }

}
