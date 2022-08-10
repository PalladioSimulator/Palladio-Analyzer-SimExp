package org.palladiosimulator.simexp.dsl.kmodel.interpreter;

import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Probe;

public class DummyProbeValueProvider implements ProbeValueProvider, ProbeValueProviderMeasurementInjector {
    
    private double currentMeasurementValue;
    

    @Override
    public Object getValue(Probe probe) {
        // TODO: lookup current measured responseTime from probe instead of using ProbeValueProviderMeasurementInjector
        return currentMeasurementValue;
    }
    
    public void injectMeasurement(double measurementValue) {
        this.currentMeasurementValue = measurementValue;
    }

}
