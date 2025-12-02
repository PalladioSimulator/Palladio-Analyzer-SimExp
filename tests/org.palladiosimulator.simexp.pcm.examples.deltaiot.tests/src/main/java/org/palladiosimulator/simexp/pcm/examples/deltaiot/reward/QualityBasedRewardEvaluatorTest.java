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
    public void testEvaluate() {
        SimulatedMeasurement packetLossMeasurement = SimulatedMeasurement.of(0.06, packetLossSpec);
        SimulatedMeasurement energyConsumptionMeasurement = SimulatedMeasurement.of(31.9, energyConsumptionSpec);
        List<SimulatedMeasurement> measuredQuantities = Arrays.asList(packetLossMeasurement,
                energyConsumptionMeasurement);
        StateQuantity quantifiedState = StateQuantity.of(measuredQuantities);

        Reward<Double> actualReward = evaluator.evaluate(quantifiedState);

        assertThat(actualReward.getValue()).isEqualTo(1.45, withPrecision(EPSILON));
    }

}
