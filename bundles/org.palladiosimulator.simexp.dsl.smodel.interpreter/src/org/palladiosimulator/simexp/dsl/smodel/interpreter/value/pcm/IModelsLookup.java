package org.palladiosimulator.simexp.dsl.smodel.interpreter.value.pcm;

import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;

public interface IModelsLookup {

    MeasuringPoint findMeasuringPoint(Probe probe);

}