package org.palladiosimulator.simexp.dsl.kmodel.interpreter;

import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Probe;

public interface IMeasuringPointFinder {
    MeasuringPoint find(Experiment experiment, Probe probe);
}
