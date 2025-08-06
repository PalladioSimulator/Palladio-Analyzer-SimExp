package org.palladiosimulator.simexp.core.quality;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;

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
        List<Map<String, List<Double>>> actualQualityAttributes = evaluator.getQualityAttributes();

        assertThat(actualQualityAttributes).isNull();
    }
}
