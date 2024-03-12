package org.palladiosimulator.simexp.dsl.kmodel.interpreter;

import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Probe;

public interface MeasuringPointLookup {

    MeasuringPoint find(Probe probe);

}
