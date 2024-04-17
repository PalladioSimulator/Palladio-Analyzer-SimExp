package org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.value;

import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;

public interface IMeasuringPointFinder {
    MeasuringPoint find(Experiment experiment, Probe probe);
}
