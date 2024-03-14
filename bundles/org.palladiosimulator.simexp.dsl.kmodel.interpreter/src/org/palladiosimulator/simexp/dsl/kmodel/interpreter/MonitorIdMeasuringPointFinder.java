package org.palladiosimulator.simexp.dsl.kmodel.interpreter;

import java.util.stream.Stream;

import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.monitorrepository.Monitor;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Probe;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.ProbeAdressingKind;

public class MonitorIdMeasuringPointFinder implements IMeasuringPointFinder {

    @Override
    public MeasuringPoint find(Experiment experiment, Probe probe) {
        if (probe.getKind() != ProbeAdressingKind.MONITORID) {
            throw new RuntimeException(String.format("Wrong probe addressing mode. Expected '%s', but found '%s'",
                    ProbeAdressingKind.MONITORID.getLiteral(), probe.getKind()
                        .getLiteral()));
        }
        Monitor monitor = findMonitor(experiment, probe.getIdentifier());
        MeasuringPoint measuringPoint = monitor.getMeasuringPoint();
        return measuringPoint;
    }

    private Monitor findMonitor(Experiment experiment, String monitorId) {
        Stream<Monitor> monitors = experiment.getInitialModel()
            .getMonitorRepository()
            .getMonitors()
            .stream();
        return monitors.filter(m -> m.getId()
            .equals(monitorId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException(String.format("No monitor with id: '%s' found", monitorId)));
    }
}
