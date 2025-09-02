package org.palladiosimulator.simexp.core.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;

public abstract class SimulatedMeasurementSpecification {

    private final String id;
    private final String name;

    public SimulatedMeasurementSpecification(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
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
        SimulatedMeasurementSpecification rhs = (SimulatedMeasurementSpecification) obj;
        return new EqualsBuilder() //
            .append(name, rhs.name)
            .isEquals();
    }

    @Override
    public String toString() {
        return getName();
    }

}
