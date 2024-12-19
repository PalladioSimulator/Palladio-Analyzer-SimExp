package org.palladiosimulator.simexp.core.state;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;

public class SpecialCaseStateQuantityTest {
    private static final String SPEC_ID = "PacketLoss.prism";
    private static final String SPEC_NAME = "PacketLoss.props";

    private SpecialCaseStateQuantity quantity;

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
        simulatedMeasurement = SimulatedMeasurement.of(0.1, specification);

        String quantifiedState = "[{value: 2.0, spec: EnergyConsumption.props}|{value: 0.1, spec: PacketLoss.props}]";
        quantity = new SpecialCaseStateQuantity(quantifiedState);
    }

    @Test
    public void findMeasurementWith() {
        SimulatedMeasurementSpecification spec = new TestSimulatedMeasurementSpecification(SPEC_ID, SPEC_NAME);

        Optional<SimulatedMeasurement> actualMeasurement = quantity.findMeasurementWith(spec);

        assertThat(actualMeasurement).hasValue(simulatedMeasurement);
    }
}
