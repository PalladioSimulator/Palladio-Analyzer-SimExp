package org.palladiosimulator.simexp.dsl.smodel.interpreter;

import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;

public interface ProbeValueProviderMeasurementInjector {

    void injectMeasurement(SimulatedMeasurementSpecification spec, double measurementValue);

}
