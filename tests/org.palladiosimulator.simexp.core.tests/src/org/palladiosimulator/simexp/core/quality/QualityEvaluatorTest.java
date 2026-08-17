package org.palladiosimulator.simexp.core.quality;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator.QualityMeasurements;
import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator.Run;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.state.StateQuantity;

public class QualityEvaluatorTest {
    private QualityEvaluator evaluator;

    @Mock
    private SimulatedMeasurementSpecification measurementSpecification1;
    @Mock
    private StateQuantity quantifiedState1;
    @Mock
    private SelfAdaptiveSystemState<?, ?, ?> sasState1;
    @Mock
    private SimulatedMeasurement measurement1;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        when(measurementSpecification1.getName()).thenReturn("qa1");
        when(sasState1.getQuantifiedState()).thenReturn(quantifiedState1);
        when(quantifiedState1.findMeasurementWith(measurementSpecification1)).thenReturn(Optional.of(measurement1));
        when(measurement1.getValue()).thenReturn(1.0);

        evaluator = new QualityEvaluator(Arrays.asList(measurementSpecification1));
    }

    @Test
    public void testNoRun() {
        QualityMeasurements actualQualityMeasurements = evaluator.getQualityMeasurements();

        QualityMeasurements expectedQualityMeasurements = new QualityMeasurements(Collections.emptyList());
        assertThat(actualQualityMeasurements).usingRecursiveComparison()
            .isEqualTo(expectedQualityMeasurements);
    }

    @Test
    public void testOneRunOneValue() {
        evaluator.initialize();
        evaluator.monitor(sasState1);

        QualityMeasurements actualQualityMeasurements = evaluator.getQualityMeasurements();

        Map<String, List<Double>> expectedQualityAttributes1 = new HashMap<>();
        expectedQualityAttributes1.put(measurementSpecification1.getName(), Arrays.asList(1.0));
        List<Run> runs = Arrays.asList(new Run(expectedQualityAttributes1));
        QualityMeasurements expectedQualityMeasurements = new QualityMeasurements(runs);
        assertThat(actualQualityMeasurements).usingRecursiveComparison()
            .isEqualTo(expectedQualityMeasurements);
    }

    @Test
    public void testOneRunTwoValue() {
        evaluator.initialize();
        evaluator.monitor(sasState1);
        when(measurement1.getValue()).thenReturn(2.0);
        evaluator.monitor(sasState1);

        QualityMeasurements actualQualityMeasurements = evaluator.getQualityMeasurements();

        Map<String, List<Double>> expectedQualityAttributes1 = new HashMap<>();
        expectedQualityAttributes1.put(measurementSpecification1.getName(), Arrays.asList(1.0, 2.0));
        List<Run> runs = Arrays.asList(new Run(expectedQualityAttributes1));
        QualityMeasurements expectedQualityMeasurements = new QualityMeasurements(runs);
        assertThat(actualQualityMeasurements).usingRecursiveComparison()
            .isEqualTo(expectedQualityMeasurements);
    }

    @Test
    public void testTwoRunOneValue() {
        evaluator.initialize();
        evaluator.monitor(sasState1);
        evaluator.initialize();
        when(measurement1.getValue()).thenReturn(2.0);
        evaluator.monitor(sasState1);

        QualityMeasurements actualQualityMeasurements = evaluator.getQualityMeasurements();

        Map<String, List<Double>> expectedQualityAttributes1 = new HashMap<>();
        expectedQualityAttributes1.put(measurementSpecification1.getName(), Arrays.asList(1.0));
        Map<String, List<Double>> expectedQualityAttributes2 = new HashMap<>();
        expectedQualityAttributes2.put(measurementSpecification1.getName(), Arrays.asList(2.0));
        List<Run> runs = Arrays.asList(new Run(expectedQualityAttributes1), new Run(expectedQualityAttributes2));
        QualityMeasurements expectedQualityMeasurements = new QualityMeasurements(runs);
        assertThat(actualQualityMeasurements).usingRecursiveComparison()
            .isEqualTo(expectedQualityMeasurements);
    }
}
