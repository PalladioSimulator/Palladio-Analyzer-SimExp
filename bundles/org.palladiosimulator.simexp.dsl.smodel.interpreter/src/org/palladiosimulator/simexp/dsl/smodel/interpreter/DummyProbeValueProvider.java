package org.palladiosimulator.simexp.dsl.smodel.interpreter;

import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;

public class DummyProbeValueProvider implements ProbeValueProvider, ProbeValueProviderMeasurementInjector {

    private double currentMeasurementValue;

    @Override
    public Object getValue(Probe probe) {
        // TODO: lookup current measured responseTime from probe instead of using
        // ProbeValueProviderMeasurementInjector
        return currentMeasurementValue;
    }

    @Override
    public void injectMeasurement(SimulatedMeasurementSpecification spec, double measurementValue) {
        this.currentMeasurementValue = measurementValue;
    }

}
