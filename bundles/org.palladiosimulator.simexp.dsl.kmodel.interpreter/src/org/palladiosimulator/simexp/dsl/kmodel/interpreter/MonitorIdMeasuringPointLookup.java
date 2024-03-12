package org.palladiosimulator.simexp.dsl.kmodel.interpreter;

import java.util.stream.Stream;

import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.monitorrepository.Monitor;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Probe;

public class MonitorIdMeasuringPointLookup implements MeasuringPointLookup {

    private final Experiment experiment;

    public MonitorIdMeasuringPointLookup(Experiment experiment) {
        this.experiment = experiment;
    }

    @Override
    public MeasuringPoint find(Probe probe) {
        Monitor monitor = findMonitor(probe.getIdentifier());
        MeasuringPoint measuringPoint = monitor.getMeasuringPoint();
        return measuringPoint;
    }

    private Monitor findMonitor(String monitorId) {
        Stream<Monitor> monitors = experiment.getInitialModel()
            .getMonitorRepository()
            .getMonitors()
            .stream();
        return monitors.filter(m -> m.getId()
            .equals(monitorId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("There is no monitor."));
    }

}
