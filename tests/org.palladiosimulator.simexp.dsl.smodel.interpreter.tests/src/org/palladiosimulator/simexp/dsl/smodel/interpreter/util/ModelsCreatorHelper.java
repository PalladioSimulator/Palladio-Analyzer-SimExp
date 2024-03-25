package org.palladiosimulator.simexp.dsl.smodel.interpreter.util;

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
import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ProbeAdressingKind;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelFactory;

public class ModelsCreatorHelper {
    public static final String RESPONSE_TIME_MONITOR_NAME = "System Response Time";
    public static final String USAGE_SCENARIO_NAME = "testUsageScenario";
    public static final String RESPONSE_TIME_METRIC_DESCRIPTION_NAME = "Response Time";

    private static final ExperimentsFactory experimentsFactory = ExperimentsFactory.eINSTANCE;
    private static final MonitorRepositoryFactory monitorRepoFactory = MonitorRepositoryFactory.eINSTANCE;
    private static final SmodelFactory smodelFactory = SmodelFactory.eINSTANCE;

    public Experiment createBasicExperiment() {
        Experiment experiment = experimentsFactory.createExperiment();
        return experiment;
    }

    public Experiment createExperimentWithMonitor(Monitor monitor) {
        Experiment experiment = createBasicExperiment();
        MonitorRepository monitorRepo = monitorRepoFactory.createMonitorRepository();
        monitorRepo.getMonitors()
            .add(monitor);
        InitialModel initialModel = experimentsFactory.createInitialModel();
        initialModel.setMonitorRepository(monitorRepo);
        experiment.setInitialModel(initialModel);
        return experiment;
    }

    public Monitor createResponseTimeMonitor(String monitorName, String usageScenarioName, String metricDescrName) {
        Monitor monitor = MonitorRepositoryFactory.eINSTANCE.createMonitor();
        monitor.setEntityName(monitorName);

        // create response time measuring point
        UsageScenarioMeasuringPoint measuringPoint = PcmmeasuringpointFactory.eINSTANCE
            .createUsageScenarioMeasuringPoint();
        UsageScenario usageScenario = UsagemodelFactory.eINSTANCE.createUsageScenario();
        usageScenario.setEntityName(usageScenarioName);
        measuringPoint.setUsageScenario(usageScenario);
        monitor.setMeasuringPoint(measuringPoint);

        MeasurementSpecification spec = MonitorRepositoryFactory.eINSTANCE.createMeasurementSpecification();
        NumericalBaseMetricDescription metricDescription = MetricSpecFactory.eINSTANCE
            .createNumericalBaseMetricDescription();
        metricDescription.setName(metricDescrName);
        spec.setMetricDescription(metricDescription);
        monitor.getMeasurementSpecifications()
            .add(spec);

        return monitor;
    }

    public Probe createProbe(String monitorId, ProbeAdressingKind value) {
        Probe probe = smodelFactory.createProbe();
        probe.setName("testProbe");
        probe.setIdentifier(monitorId);
        probe.setKind(value);
        return probe;
    }

}
