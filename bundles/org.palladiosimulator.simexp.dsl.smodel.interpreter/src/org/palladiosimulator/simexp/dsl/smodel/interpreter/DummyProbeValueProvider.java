package org.palladiosimulator.simexp.dsl.smodel.interpreter;

import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;

public class DummyProbeValueProvider implements ProbeValueProvider, ProbeValueProviderMeasurementInjector {

    private double currentMeasurementValue;

    @Override
    public double getDoubleValue(Probe probe) {
        return currentMeasurementValue;
    }

    @Override
    public void injectMeasurement(SimulatedMeasurementSpecification spec, double measurementValue) {
        this.currentMeasurementValue = measurementValue;
    }

    @Override
    public boolean getBooleanValue(Probe probe) {
        // TODO Auto-generated method stub
        return false;
    }

}
