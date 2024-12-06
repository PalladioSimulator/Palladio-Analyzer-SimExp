package org.palladiosimulator.simexp.core.state;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;

public class StateQuantityTest {
    private StateQuantity quantity;

    private SimulatedMeasurementSpecification specification;
    private SimulatedMeasurement simulatedMeasurement;

    private static class TestSimulatedMeasurementSpecification extends SimulatedMeasurementSpecification {
        public TestSimulatedMeasurementSpecification(String id, String name) {
            super(id, name);
        }
    }

    @Before
    public void setUp() throws Exception {
        specification = new TestSimulatedMeasurementSpecification("tspec", "spec");
        simulatedMeasurement = SimulatedMeasurement.of(1.0, specification);

        quantity = StateQuantity.of(Collections.singletonList(simulatedMeasurement));
    }

    @Test
    public void findMeasurementWith() {
        SimulatedMeasurementSpecification spec = specification;

        Optional<SimulatedMeasurement> actualMeasurement = quantity.findMeasurementWith(spec);

        assertThat(actualMeasurement).containsSame(simulatedMeasurement);
    }

}
