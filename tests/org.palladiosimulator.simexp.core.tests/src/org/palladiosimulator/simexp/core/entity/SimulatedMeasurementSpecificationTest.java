package org.palladiosimulator.simexp.core.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class SimulatedMeasurementSpecificationTest {
    private static class TestSimulatedMeasurementSpecification extends SimulatedMeasurementSpecification {

        public TestSimulatedMeasurementSpecification(String id, String name) {
            super(id, name);
        }

    }

    @Test
    public void testEqualsName() {
        TestSimulatedMeasurementSpecification s1 = new TestSimulatedMeasurementSpecification("1", "a");
        TestSimulatedMeasurementSpecification s2 = new TestSimulatedMeasurementSpecification("2", "a");

        boolean actualEquals = s1.equals(s2);

        assertThat(actualEquals).isTrue();
    }

    @Test
    public void testNotEqualsName() {
        TestSimulatedMeasurementSpecification s1 = new TestSimulatedMeasurementSpecification("1", "a");
        TestSimulatedMeasurementSpecification s2 = new TestSimulatedMeasurementSpecification("1", "b");

        boolean actualEquals = s1.equals(s2);

        assertThat(actualEquals).isFalse();
    }
}
