package org.palladiosimulator.simexp.dsl.kmodel.interpreter.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.monitorrepository.Monitor;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.MonitorIdMeasuringPointFinder;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.util.ModelsCreatorHelper;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Probe;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.ProbeAdressing;

public class MonitorIdMeasuringPointFinderTest {

    MonitorIdMeasuringPointFinder mpLookup;
    ModelsCreatorHelper modelsCreator;

    @Before
    public void setUp() throws Exception {
        mpLookup = new MonitorIdMeasuringPointFinder();
        modelsCreator = new ModelsCreatorHelper();
    }

    @Test
    public void testWrongProbeAddressing() throws Exception {
        Experiment experiment = modelsCreator.createBasicExperiment();
        Probe probe = modelsCreator.createProbe("any", ProbeAdressing.ID);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> mpLookup.find(experiment, probe));

        String expectedMessage = "Wrong probe addressing mode. Expected 'monitorId', but found 'id'";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testUnknownMonitorId() throws Exception {
        Monitor rtMonitor = modelsCreator.createResponseTimeMonitor(ModelsCreatorHelper.RESPONSE_TIME_MONITOR_NAME,
                ModelsCreatorHelper.USAGE_SCENARIO_NAME, ModelsCreatorHelper.RESPONSE_TIME_METRIC_DESCRIPTION_NAME);
        Experiment experiment = modelsCreator.createExperimentWithMonitor(rtMonitor);
        Probe probe = modelsCreator.createProbe("unknownMonitorId", ProbeAdressing.MONITORID);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> mpLookup.find(experiment, probe));

        String expectedMessage = "No monitor with id: 'unknownMonitorId' found";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testKnownMonitorIdResponseTime() {
        Monitor rtMonitor = modelsCreator.createResponseTimeMonitor(ModelsCreatorHelper.RESPONSE_TIME_MONITOR_NAME,
                ModelsCreatorHelper.USAGE_SCENARIO_NAME, ModelsCreatorHelper.RESPONSE_TIME_METRIC_DESCRIPTION_NAME);
        Experiment experiment = modelsCreator.createExperimentWithMonitor(rtMonitor);
        Probe probe = modelsCreator.createProbe(rtMonitor.getId(), ProbeAdressing.MONITORID);

        MeasuringPoint actualMeasuringPoint = mpLookup.find(experiment, probe);

        assertEquals(rtMonitor.getMeasuringPoint(), actualMeasuringPoint);
    }

}
