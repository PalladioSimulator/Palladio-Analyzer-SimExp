package org.palladiosimulator.simexp.core.state;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;

public class StateQuantity {

    protected final static String DELIMITER = "|";

    private final LinkedHashSet<SimulatedMeasurement> measuredQuantities;

    public static StateQuantity of(List<SimulatedMeasurement> measuredQuantities) {
        return new StateQuantity(new LinkedHashSet<>(measuredQuantities));
    }

    public static StateQuantity with(List<SimulatedMeasurementSpecification> specs) {
        List<SimulatedMeasurement> quantities = specs.stream()
            .map(SimulatedMeasurement::with)
            .collect(Collectors.toList());
        return of(quantities);
    }

    protected StateQuantity(LinkedHashSet<SimulatedMeasurement> measuredQuantities) {
        this.measuredQuantities = measuredQuantities;
    }

    public Set<SimulatedMeasurementSpecification> getMeasurementSpecs() {
        return measuredQuantities.stream()
            .map(each -> each.getSpecification())
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<SimulatedMeasurement> getMeasurements() {
        return measuredQuantities;
    }

    public Optional<SimulatedMeasurement> findMeasurementWith(SimulatedMeasurementSpecification spec) {
        return measuredQuantities.stream()
            .filter(each -> haveSameSpecification(each, spec))
            .findFirst();
    }

    public void setMeasurement(double newValue, SimulatedMeasurementSpecification spec) {
        Optional<SimulatedMeasurement> result = findMeasurementWith(spec);
        if (result.isPresent()) {
            result.get()
                .setValue(newValue);
        } else {
            // Logging in case that nothing has been updated
        }
    }

    private boolean haveSameSpecification(SimulatedMeasurement simMeasurement, SimulatedMeasurementSpecification spec) {
        return haveSameSpecification(simMeasurement.getSpecification(), spec);
    }

    private boolean haveSameSpecification(SimulatedMeasurementSpecification first,
            SimulatedMeasurementSpecification second) {
        return first.getId()
            .equals(second.getId());
    }

    @Override
    public String toString() {
        return String.format("[%s]", getQuantitiesAsString());
    }

    private String getQuantitiesAsString() {
        StringBuilder builder = new StringBuilder();
        for (SimulatedMeasurement each : measuredQuantities) {
            builder.append(each.toString());
            builder.append(DELIMITER);
        }
        builder.deleteCharAt(builder.lastIndexOf(DELIMITER));
        return builder.toString();
    }

}
