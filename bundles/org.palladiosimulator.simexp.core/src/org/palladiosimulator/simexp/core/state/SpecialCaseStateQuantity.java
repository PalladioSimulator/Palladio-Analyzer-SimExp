package org.palladiosimulator.simexp.core.state;

import java.util.Optional;
import java.util.stream.Stream;

import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;

/**
 * TODO this is not a good solution: The class should be refactored in further iterations.
 */
public class SpecialCaseStateQuantity extends StateQuantity {

    private final String quantifiedState;

    public SpecialCaseStateQuantity(String quantifiedState) {
        super(null);
        this.quantifiedState = quantifiedState;
    }

    @Override
    public Optional<SimulatedMeasurement> findMeasurementWith(SimulatedMeasurementSpecification spec) {
        String[] tokens = quantifiedState.split(withDelimiter());
        Stream<String> quantities = Stream.of(tokens);
        return quantities.filter(quantity -> quantity.contains(spec.getName()))
            .map(quantity -> restoreSimulatedMeasurement(quantity, spec))
            .findFirst();
    }

    private String withDelimiter() {
        return "\\" + DELIMITER;
    }

    private SimulatedMeasurement restoreSimulatedMeasurement(String quantity, SimulatedMeasurementSpecification spec) {
        Double restoredValue = restoreValue(quantity);
        SimulatedMeasurement measurement = SimulatedMeasurement.of(restoredValue, spec);
        return measurement;
    }

    private Double restoreValue(String quantity) {
        String value = quantity.split(" ")[1];
        value = value.replace(",", "");
        return Double.parseDouble(value);
    }

    @Override
    public String toString() {
        return quantifiedState;
    }

}
