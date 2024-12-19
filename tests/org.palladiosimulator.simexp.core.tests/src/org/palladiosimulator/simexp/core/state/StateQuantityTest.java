package org.palladiosimulator.simexp.core.state;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;

public class StateQuantityTest {
    private static final String SPEC_NAME = "spec";
    private static final String SPEC_ID = "tspec";

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
        specification = new TestSimulatedMeasurementSpecification(SPEC_ID, SPEC_NAME);
        simulatedMeasurement = SimulatedMeasurement.of(1.0, specification);

        quantity = StateQuantity.of(Collections.singletonList(simulatedMeasurement));
    }

    @Test
    public void findMeasurementWith() {
        SimulatedMeasurementSpecification spec = new TestSimulatedMeasurementSpecification(SPEC_ID, SPEC_NAME);

        Optional<SimulatedMeasurement> actualMeasurement = quantity.findMeasurementWith(spec);

        assertThat(actualMeasurement).containsSame(simulatedMeasurement);
    }

}
