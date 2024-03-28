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

        MeasuringPoint find = finder.find(experiment, probe);
        if (find == null) {
            throw new RuntimeException(String.format("No MeasuringPoint found for probe '%s' with %s:%s found",
                    probe.getName(), probe.getKind(), probe.getIdentifier()));
        }
        return find;
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
