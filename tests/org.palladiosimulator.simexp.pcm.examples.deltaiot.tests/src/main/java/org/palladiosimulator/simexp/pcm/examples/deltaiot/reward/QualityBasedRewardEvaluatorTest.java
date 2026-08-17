package org.palladiosimulator.simexp.pcm.examples.deltaiot.reward;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.state.StateQuantity;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Reward;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTCommons;

public class QualityBasedRewardEvaluatorTest {
    private static final double EPSILON = 0.0001;

    private QualityBasedRewardEvaluator evaluator;

    @Mock
    private SimulatedMeasurementSpecification packetLossSpec;
    @Mock
    private SimulatedMeasurementSpecification energyConsumptionSpec;

    @Before
    public void setUp() {
        initMocks(this);

        when(packetLossSpec.getId()).thenReturn("packetLossSpec");
        when(energyConsumptionSpec.getId()).thenReturn("energyConsumptionSpec");

        evaluator = new QualityBasedRewardEvaluator(packetLossSpec, energyConsumptionSpec);
    }

    @Test
    public void testNormalizeInRange() {
        double actualValue = evaluator.normalize(0.5, 0.0, 1.0, "");

        assertThat(actualValue).isEqualTo(0.5, withPrecision(EPSILON));
    }

    @Test
    public void testNormalizeTooLow() {
        double actualValue = evaluator.normalize(0.1, 0.5, 1.0, "");

        assertThat(actualValue).isEqualTo(1.0, withPrecision(EPSILON));
    }

    @Test
    public void testNormalizeBorderLow() {
        double actualValue = evaluator.normalize(0.0, 0.0, 1.0, "");

        assertThat(actualValue).isEqualTo(1.0, withPrecision(EPSILON));
    }

    @Test
    public void testNormalizeTooHigh() {
        double actualValue = evaluator.normalize(1.1, 0.0, 1.0, "");

        assertThat(actualValue).isEqualTo(0.0, withPrecision(EPSILON));
    }

    @Test
    public void testNormalizeBorderHigh() {
        double actualValue = evaluator.normalize(1.0, 0.0, 1.0, "");

        assertThat(actualValue).isEqualTo(0.0, withPrecision(EPSILON));
    }

    @Test
    public void testEvaluateInRange() {
        SimulatedMeasurement packetLossMeasurement = SimulatedMeasurement
            .of(DeltaIoTCommons.LOWER_BOUND_PACKET_LOSS + 0.1, packetLossSpec);
        SimulatedMeasurement energyConsumptionMeasurement = SimulatedMeasurement
            .of(DeltaIoTCommons.LOWER_BOUND_ENERGY_CONSUMPTION + 0.1, energyConsumptionSpec);
        List<SimulatedMeasurement> measuredQuantities = Arrays.asList(packetLossMeasurement,
                energyConsumptionMeasurement);
        StateQuantity quantifiedState = StateQuantity.of(measuredQuantities);

        Reward<Double> actualReward = evaluator.evaluate(quantifiedState);

        assertThat(actualReward.getValue()).isEqualTo(1.731, withPrecision(EPSILON));
    }

    @Test
    public void testEvaluatePLLow() {
        SimulatedMeasurement packetLossMeasurement = SimulatedMeasurement
            .of(DeltaIoTCommons.LOWER_BOUND_PACKET_LOSS - 0.01, packetLossSpec);
        SimulatedMeasurement energyConsumptionMeasurement = SimulatedMeasurement
            .of(DeltaIoTCommons.LOWER_BOUND_ENERGY_CONSUMPTION, energyConsumptionSpec);
        List<SimulatedMeasurement> measuredQuantities = Arrays.asList(packetLossMeasurement,
                energyConsumptionMeasurement);
        StateQuantity quantifiedState = StateQuantity.of(measuredQuantities);

        Reward<Double> actualReward = evaluator.evaluate(quantifiedState);

        assertThat(actualReward.getValue()).isEqualTo(2.0, withPrecision(EPSILON));
    }

    @Test
    public void testEvaluatePLHigh() {
        SimulatedMeasurement packetLossMeasurement = SimulatedMeasurement
            .of(DeltaIoTCommons.UPPER_BOUND_PACKET_LOSS + 0.01, packetLossSpec);
        SimulatedMeasurement energyConsumptionMeasurement = SimulatedMeasurement
            .of(DeltaIoTCommons.LOWER_BOUND_ENERGY_CONSUMPTION, energyConsumptionSpec);
        List<SimulatedMeasurement> measuredQuantities = Arrays.asList(packetLossMeasurement,
                energyConsumptionMeasurement);
        StateQuantity quantifiedState = StateQuantity.of(measuredQuantities);

        Reward<Double> actualReward = evaluator.evaluate(quantifiedState);

        assertThat(actualReward.getValue()).isEqualTo(1.0, withPrecision(EPSILON));
    }

    @Test
    public void testEvaluateECLow() {
        SimulatedMeasurement packetLossMeasurement = SimulatedMeasurement.of(DeltaIoTCommons.LOWER_BOUND_PACKET_LOSS,
                packetLossSpec);
        SimulatedMeasurement energyConsumptionMeasurement = SimulatedMeasurement
            .of(DeltaIoTCommons.LOWER_BOUND_ENERGY_CONSUMPTION - 0.1, energyConsumptionSpec);
        List<SimulatedMeasurement> measuredQuantities = Arrays.asList(packetLossMeasurement,
                energyConsumptionMeasurement);
        StateQuantity quantifiedState = StateQuantity.of(measuredQuantities);

        Reward<Double> actualReward = evaluator.evaluate(quantifiedState);

        assertThat(actualReward.getValue()).isEqualTo(2.0, withPrecision(EPSILON));
    }

    @Test
    public void testEvaluateECHigh() {
        SimulatedMeasurement packetLossMeasurement = SimulatedMeasurement.of(DeltaIoTCommons.LOWER_BOUND_PACKET_LOSS,
                packetLossSpec);
        SimulatedMeasurement energyConsumptionMeasurement = SimulatedMeasurement
            .of(DeltaIoTCommons.UPPER_BOUND_ENERGY_CONSUMPTION + 0.1, energyConsumptionSpec);
        List<SimulatedMeasurement> measuredQuantities = Arrays.asList(packetLossMeasurement,
                energyConsumptionMeasurement);
        StateQuantity quantifiedState = StateQuantity.of(measuredQuantities);

        Reward<Double> actualReward = evaluator.evaluate(quantifiedState);

        assertThat(actualReward.getValue()).isEqualTo(1.0, withPrecision(EPSILON));
    }

    @Test
    public void testEvaluatePLBorderLow() {
        SimulatedMeasurement packetLossMeasurement = SimulatedMeasurement.of(DeltaIoTCommons.LOWER_BOUND_PACKET_LOSS,
                packetLossSpec);
        SimulatedMeasurement energyConsumptionMeasurement = SimulatedMeasurement
            .of(DeltaIoTCommons.LOWER_BOUND_ENERGY_CONSUMPTION, energyConsumptionSpec);
        List<SimulatedMeasurement> measuredQuantities = Arrays.asList(packetLossMeasurement,
                energyConsumptionMeasurement);
        StateQuantity quantifiedState = StateQuantity.of(measuredQuantities);

        Reward<Double> actualReward = evaluator.evaluate(quantifiedState);

        assertThat(actualReward.getValue()).isEqualTo(2.0, withPrecision(EPSILON));
    }

    @Test
    public void testEvaluatePLBorderHigh() {
        SimulatedMeasurement packetLossMeasurement = SimulatedMeasurement.of(DeltaIoTCommons.UPPER_BOUND_PACKET_LOSS,
                packetLossSpec);
        SimulatedMeasurement energyConsumptionMeasurement = SimulatedMeasurement
            .of(DeltaIoTCommons.LOWER_BOUND_ENERGY_CONSUMPTION, energyConsumptionSpec);
        List<SimulatedMeasurement> measuredQuantities = Arrays.asList(packetLossMeasurement,
                energyConsumptionMeasurement);
        StateQuantity quantifiedState = StateQuantity.of(measuredQuantities);

        Reward<Double> actualReward = evaluator.evaluate(quantifiedState);

        assertThat(actualReward.getValue()).isEqualTo(1.0, withPrecision(EPSILON));
    }

    @Test
    public void testEvaluateECBorderLow() {
        SimulatedMeasurement packetLossMeasurement = SimulatedMeasurement.of(DeltaIoTCommons.LOWER_BOUND_PACKET_LOSS,
                packetLossSpec);
        SimulatedMeasurement energyConsumptionMeasurement = SimulatedMeasurement
            .of(DeltaIoTCommons.LOWER_BOUND_ENERGY_CONSUMPTION, energyConsumptionSpec);
        List<SimulatedMeasurement> measuredQuantities = Arrays.asList(packetLossMeasurement,
                energyConsumptionMeasurement);
        StateQuantity quantifiedState = StateQuantity.of(measuredQuantities);

        Reward<Double> actualReward = evaluator.evaluate(quantifiedState);

        assertThat(actualReward.getValue()).isEqualTo(2.0, withPrecision(EPSILON));
    }

    @Test
    public void testEvaluateECBorderHigh() {
        SimulatedMeasurement packetLossMeasurement = SimulatedMeasurement.of(DeltaIoTCommons.LOWER_BOUND_PACKET_LOSS,
                packetLossSpec);
        SimulatedMeasurement energyConsumptionMeasurement = SimulatedMeasurement
            .of(DeltaIoTCommons.UPPER_BOUND_ENERGY_CONSUMPTION, energyConsumptionSpec);
        List<SimulatedMeasurement> measuredQuantities = Arrays.asList(packetLossMeasurement,
                energyConsumptionMeasurement);
        StateQuantity quantifiedState = StateQuantity.of(measuredQuantities);

        Reward<Double> actualReward = evaluator.evaluate(quantifiedState);

        assertThat(actualReward.getValue()).isEqualTo(1.0, withPrecision(EPSILON));
    }
}
