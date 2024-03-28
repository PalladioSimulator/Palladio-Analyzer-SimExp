package org.palladiosimulator.simexp.dsl.smodel.interpreter;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.monitorrepository.Monitor;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ProbeAdressingKind;

public class MonitorIdMeasuringPointFinder implements IMeasuringPointFinder {

    @Override
    public MeasuringPoint find(Experiment experiment, Probe probe) {
        if (probe.getKind() != ProbeAdressingKind.MONITORID) {
            throw new RuntimeException(String.format("Wrong probe addressing mode. Expected '%s', but found '%s'",
                    ProbeAdressingKind.MONITORID.getLiteral(), probe.getKind()
                        .getLiteral()));
        }
        Monitor monitor = findMonitor(experiment, probe.getIdentifier());
        if (monitor == null) {
            return null;
        }
        MeasuringPoint measuringPoint = monitor.getMeasuringPoint();
        return measuringPoint;
    }

    private Monitor findMonitor(Experiment experiment, String monitorId) {
        EList<Monitor> monitors = experiment.getInitialModel()
            .getMonitorRepository()
            .getMonitors();
        if (monitors == null || monitors.isEmpty()) {
            return null;
        }
        for (Monitor monitor : monitors) {
            if (monitor.getId()
                .equals(monitorId)) {
                return monitor;
            }
        }
        return null;
    }
}
