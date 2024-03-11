package org.palladiosimulator.simexp.dsl.kmodel.interpreter.tests;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.metricspec.MetricSpecFactory;
import org.palladiosimulator.metricspec.NumericalBaseMetricDescription;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.monitorrepository.Monitor;
import org.palladiosimulator.monitorrepository.MonitorRepositoryFactory;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.pcm.usagemodel.UsagemodelFactory;
import org.palladiosimulator.pcmmeasuringpoint.PcmmeasuringpointFactory;
import org.palladiosimulator.pcmmeasuringpoint.UsageScenarioMeasuringPoint;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.PcmProbeValueProvider;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.KmodelFactory;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Probe;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;

public class PcmProbeValueProviderTest {

    private static final double DELTA = 1e-15;

    private static final String MONITOR_NAME = "System Response Time";
    private static final String USAGE_SCENARIO_NAME = "testUsageScenario";
    private static final String MEASURING_POINT_ID = "Usage Scenario: " + USAGE_SCENARIO_NAME;
    private static final String METRIC_DESCRIPTION_NAME = "Response Time";
//    private static final String MEASURING_POINT_ID_RESPONSE_TIME = "Usage Scenario: overloadUsageScenario_Response Time";

    PcmProbeValueProvider pvp;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testGetMeasuredValueForProbeWithId() {
        PcmMeasurementSpecification responseTimeSpec = createResponseTimeSpec();
        List<PcmMeasurementSpecification> specs = Arrays.asList(responseTimeSpec);

        Probe probe = KmodelFactory.eINSTANCE.createProbe();
        probe.setIdentifier(MEASURING_POINT_ID);
        List<Probe> probes = Arrays.asList(probe);

        pvp = new PcmProbeValueProvider(probes, specs);

        double expectedValue = 0.123;
        pvp.injectMeasurement(responseTimeSpec, expectedValue);
        double measuredValue = (double) pvp.getValue(probe);

        assertEquals(expectedValue, measuredValue, DELTA);
    }

    private PcmMeasurementSpecification createResponseTimeSpec() {
        Monitor monitor = MonitorRepositoryFactory.eINSTANCE.createMonitor();
        monitor.setEntityName(MONITOR_NAME);

        // create response time measuring point
        UsageScenarioMeasuringPoint measuringPoint = PcmmeasuringpointFactory.eINSTANCE
            .createUsageScenarioMeasuringPoint();
        UsageScenario usageScenario = UsagemodelFactory.eINSTANCE.createUsageScenario();
        usageScenario.setEntityName(USAGE_SCENARIO_NAME);
        measuringPoint.setUsageScenario(usageScenario);
        monitor.setMeasuringPoint(measuringPoint);

        MeasurementSpecification spec = MonitorRepositoryFactory.eINSTANCE.createMeasurementSpecification();
        NumericalBaseMetricDescription metricDescription = MetricSpecFactory.eINSTANCE
            .createNumericalBaseMetricDescription();
        metricDescription.setName(METRIC_DESCRIPTION_NAME);
        spec.setMetricDescription(metricDescription);
        monitor.getMeasurementSpecifications()
            .add(spec);

        PcmMeasurementSpecification rtSpec = PcmMeasurementSpecification.newBuilder()
            .withName(monitor.getEntityName())
            .measuredAt(monitor.getMeasuringPoint())
            .withMetric(monitor.getMeasurementSpecifications()
                .get(0)
                .getMetricDescription())
            .useDefaultMeasurementAggregation()
            .withOptionalSteadyStateEvaluator(Threshold.lessThan(0.1))
            .build();

        return rtSpec;
    }

}
