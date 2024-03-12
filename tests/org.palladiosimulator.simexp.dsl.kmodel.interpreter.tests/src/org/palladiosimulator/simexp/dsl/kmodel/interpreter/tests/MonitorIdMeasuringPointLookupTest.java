package org.palladiosimulator.simexp.dsl.kmodel.interpreter.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.experimentautomation.experiments.ExperimentsFactory;
import org.palladiosimulator.experimentautomation.experiments.InitialModel;
import org.palladiosimulator.metricspec.MetricSpecFactory;
import org.palladiosimulator.metricspec.NumericalBaseMetricDescription;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.monitorrepository.Monitor;
import org.palladiosimulator.monitorrepository.MonitorRepository;
import org.palladiosimulator.monitorrepository.MonitorRepositoryFactory;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.pcm.usagemodel.UsagemodelFactory;
import org.palladiosimulator.pcmmeasuringpoint.PcmmeasuringpointFactory;
import org.palladiosimulator.pcmmeasuringpoint.UsageScenarioMeasuringPoint;
import org.palladiosimulator.simexp.dsl.kmodel.interpreter.MonitorIdMeasuringPointLookup;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.KmodelFactory;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Probe;

public class MonitorIdMeasuringPointLookupTest {

    private static final String MONITOR_NAME = "System Response Time";
    private static final String USAGE_SCENARIO_NAME = "testUsageScenario";
    private static final String METRIC_DESCRIPTION_NAME = "Response Time";

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testFindResponseTimeMeasurementPoint() {
        ExperimentsFactory expFactory = ExperimentsFactory.eINSTANCE;
        Experiment experiment = expFactory.createExperiment();

        MonitorRepository monitorRepo = MonitorRepositoryFactory.eINSTANCE.createMonitorRepository();
        Monitor rtMonitor = createResponseTimeMonitor();
        monitorRepo.getMonitors()
            .add(rtMonitor);
        InitialModel initialModel = expFactory.createInitialModel();
        initialModel.setMonitorRepository(monitorRepo);
        experiment.setInitialModel(initialModel);

        MonitorIdMeasuringPointLookup mpLookup = new MonitorIdMeasuringPointLookup(experiment);

        Probe probe = createProbe(rtMonitor.getId());
        MeasuringPoint actualMeasuringPoint = mpLookup.find(probe);

        assertEquals(rtMonitor.getMeasuringPoint(), actualMeasuringPoint);
    }

    private Probe createProbe(String monitorId) {
        Probe probe = KmodelFactory.eINSTANCE.createProbe();
        probe.setIdentifier(monitorId);
        return probe;
    }

    private Monitor createResponseTimeMonitor() {
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

        return monitor;
    }

}
