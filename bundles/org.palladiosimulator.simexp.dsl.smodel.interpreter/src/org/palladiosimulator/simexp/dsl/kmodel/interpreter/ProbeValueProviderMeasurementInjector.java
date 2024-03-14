package org.palladiosimulator.simexp.dsl.kmodel.interpreter;

import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;

public interface ProbeValueProviderMeasurementInjector {

    void injectMeasurement(SimulatedMeasurementSpecification spec, double measurementValue);

}
