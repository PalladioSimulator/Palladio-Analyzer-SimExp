package org.palladiosimulator.simexp.dsl.kmodel.interpreter;

import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Probe;

public interface IModelsLookup {

    MeasuringPoint findMeasuringPoint(Probe probe);

}