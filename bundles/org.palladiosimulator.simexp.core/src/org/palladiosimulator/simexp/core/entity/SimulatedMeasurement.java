package org.palladiosimulator.simexp.core.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SimulatedMeasurement {

    protected final static String VALUE_TAG = "value";
    protected final static String SPEC_TAG = "spec";

    private final SimulatedMeasurementSpecification specification;

    private double value;

    private SimulatedMeasurement(double value, SimulatedMeasurementSpecification specification) {
        this.value = value;
        this.specification = specification;
    }

    public static SimulatedMeasurement of(double value, SimulatedMeasurementSpecification specification) {
        return new SimulatedMeasurement(value, specification);
    }

    public static SimulatedMeasurement with(SimulatedMeasurementSpecification specification) {
        return new SimulatedMeasurement(Double.POSITIVE_INFINITY, specification);
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public SimulatedMeasurementSpecification getSpecification() {
        return specification;
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
        SimulatedMeasurement rhs = (SimulatedMeasurement) obj;
        return new EqualsBuilder().append(specification, rhs.specification)
            .append(value, rhs.value)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(15, 37).append(specification)
            .append(value)
            .toHashCode();
    }

    @Override
    public String toString() {
        return String.format("{%1s: %2s, %3s: %4s}", VALUE_TAG, getValue(), SPEC_TAG, getSpecification());
    }

}
