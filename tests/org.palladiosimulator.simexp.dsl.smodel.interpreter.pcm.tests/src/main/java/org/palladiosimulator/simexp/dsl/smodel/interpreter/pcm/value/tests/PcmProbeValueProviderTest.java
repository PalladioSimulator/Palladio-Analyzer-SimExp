package org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.value.tests;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.monitorrepository.Monitor;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.util.ModelsCreatorHelper;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.util.SimulatedMeasurementSpecCreatorHelper;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.value.IModelsLookup;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.value.PcmProbeValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ProbeAdressingKind;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;

public class PcmProbeValueProviderTest {

    private static final double DELTA = 1e-15;

    private PcmProbeValueProvider pvp;

    @Mock
    private IModelsLookup modelsLookup;
    private ModelsCreatorHelper modelsCreator;
    private SimulatedMeasurementSpecCreatorHelper simSpecCreator;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        pvp = new PcmProbeValueProvider(modelsLookup);
        modelsCreator = new ModelsCreatorHelper();
        simSpecCreator = new SimulatedMeasurementSpecCreatorHelper();
    }

    @Test
    public void testGetMeasuredValueForProbeWithMonitorId() {
        Probe probe = modelsCreator.createProbe(ModelsCreatorHelper.RESPONSE_TIME_MONITOR_NAME,
                ProbeAdressingKind.MONITORID);
        Monitor rtMonitor = modelsCreator.createResponseTimeMonitor(ModelsCreatorHelper.RESPONSE_TIME_MONITOR_NAME,
                ModelsCreatorHelper.USAGE_SCENARIO_NAME, ModelsCreatorHelper.RESPONSE_TIME_METRIC_DESCRIPTION_NAME);
        MeasuringPoint measuringPoint = rtMonitor.getMeasuringPoint();
        PcmMeasurementSpecification responseTimeSpec = simSpecCreator.createPcmMeasurmentSpec(rtMonitor);
        double expectedValue = 0.123;
        pvp.injectMeasurement(responseTimeSpec, expectedValue);
        when(modelsLookup.findMeasuringPoint(probe)).thenReturn(measuringPoint);

        double measuredValue = pvp.getDoubleValue(probe);

        assertEquals(expectedValue, measuredValue, DELTA);
    }

}
