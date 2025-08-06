package org.palladiosimulator.simexp.core.quality;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.simulation.IQualityEvaluator.QualityMeasurements;

public class QualityEvaluatorTest {
    private QualityEvaluator evaluator;

    @Mock
    private SimulatedMeasurementSpecification measurementSpecification;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        evaluator = new QualityEvaluator(Arrays.asList(measurementSpecification));
    }

    @Test
    public void test() {
        QualityMeasurements actualQualityMeasurements = evaluator.getQualityMeasurements();

        assertThat(actualQualityMeasurements).isNull();
    }
}
