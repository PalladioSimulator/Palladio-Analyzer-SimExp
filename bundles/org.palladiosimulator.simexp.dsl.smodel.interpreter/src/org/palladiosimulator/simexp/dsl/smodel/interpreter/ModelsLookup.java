package org.palladiosimulator.simexp.dsl.smodel.interpreter;

import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;

public class ModelsLookup implements IModelsLookup {
    private final Experiment experiment;

    public ModelsLookup(Experiment experiment) {
        this.experiment = experiment;
    }

    @Override
    public MeasuringPoint findMeasuringPoint(Probe probe) {
        IMeasuringPointFinder finder = getFinder(probe);
        return finder.find(experiment, probe);
    }

    private IMeasuringPointFinder getFinder(Probe probe) {
        return switch (probe.getKind()) {
        case MONITORID:
            yield new MonitorIdMeasuringPointFinder();
        default:
            throw new RuntimeException("probe addressing not supported: " + probe.getKind());
        };
    }

}
