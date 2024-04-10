package org.palladiosimulator.simexp.dsl.smodel.interpreter.pcm.value;

import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;

public interface ProbeValueProviderMeasurementInjector {

    void injectMeasurement(SimulatedMeasurementSpecification spec, double measurementValue);

}
