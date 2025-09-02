package org.palladiosimulator.simexp.pcm.state;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.metricspec.MetricDescription;

public class PcmMeasurementSpecificationTest {

    @Mock
    private MeasuringPoint measuringPoint;
    @Mock
    private MetricDescription metricDescription;

    @Before
    public void setUp() {
        initMocks(this);

        when(measuringPoint.getStringRepresentation()).thenReturn("mp");
        when(metricDescription.getName()).thenReturn("d");
        when(metricDescription.getId()).thenReturn("di");
    }

    @Test
    public void testSampleEquals() {
        PcmMeasurementSpecification s1 = PcmMeasurementSpecification.newBuilder()
            .withName("a")
            .measuredAt(measuringPoint)
            .withMetric(metricDescription)
            .useDefaultMeasurementAggregation()
            .build();
        PcmMeasurementSpecification s2 = PcmMeasurementSpecification.newBuilder()
            .withName("a")
            .measuredAt(measuringPoint)
            .withMetric(metricDescription)
            .useDefaultMeasurementAggregation()
            .build();

        boolean actualEquals = s1.equals(s2);

        assertThat(actualEquals).isTrue();
    }

    @Test
    public void testSampleNotEqualsName() {
        PcmMeasurementSpecification s1 = PcmMeasurementSpecification.newBuilder()
            .withName("a")
            .measuredAt(measuringPoint)
            .withMetric(metricDescription)
            .useDefaultMeasurementAggregation()
            .build();
        PcmMeasurementSpecification s2 = PcmMeasurementSpecification.newBuilder()
            .withName("b")
            .measuredAt(measuringPoint)
            .withMetric(metricDescription)
            .useDefaultMeasurementAggregation()
            .build();

        boolean actualEquals = s1.equals(s2);

        assertThat(actualEquals).isFalse();
    }
}
