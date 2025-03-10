package org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.value.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.monitorrepository.Monitor;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.util.ModelsCreatorHelper;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.value.ModelsLookup;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ProbeAdressingKind;

public class ModelsLookupTest {

    private ModelsLookup mpLookupFactory;

    private ModelsCreatorHelper modelsCreator;

    @Before
    public void setUp() throws Exception {
        this.modelsCreator = new ModelsCreatorHelper();
    }

    @Test
    public void testCreateMPLookupByMonitorId() {
        Monitor monitor = modelsCreator.createResponseTimeMonitor(ModelsCreatorHelper.RESPONSE_TIME_MONITOR_NAME,
                ModelsCreatorHelper.USAGE_SCENARIO_NAME, ModelsCreatorHelper.RESPONSE_TIME_METRIC_DESCRIPTION_NAME);
        Experiment experiment = modelsCreator.createExperimentWithMonitor(monitor);
        mpLookupFactory = new ModelsLookup(experiment);
        Probe probe = modelsCreator.createProbe(monitor.getId(), ProbeAdressingKind.MONITORID);

        MeasuringPoint actualMeasuringPoint = mpLookupFactory.findMeasuringPoint(probe);

        assertEquals(monitor.getMeasuringPoint(), actualMeasuringPoint);
    }

    @Test
    public void testNoMonitorFoundForProbe() throws Exception {
        Monitor monitor = modelsCreator.createResponseTimeMonitor(ModelsCreatorHelper.RESPONSE_TIME_MONITOR_NAME,
                ModelsCreatorHelper.USAGE_SCENARIO_NAME, ModelsCreatorHelper.RESPONSE_TIME_METRIC_DESCRIPTION_NAME);
        Experiment experiment = modelsCreator.createExperimentWithMonitor(monitor);
        mpLookupFactory = new ModelsLookup(experiment);
        Probe probe = modelsCreator.createProbe("unknownMonitorId", ProbeAdressingKind.MONITORID);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> mpLookupFactory.findMeasuringPoint(probe));

        String expectedMessage = "No MeasuringPoint found for probe 'testProbe' with monitorId:unknownMonitorId found";
        assertEquals(expectedMessage, exception.getMessage());
    }
}
