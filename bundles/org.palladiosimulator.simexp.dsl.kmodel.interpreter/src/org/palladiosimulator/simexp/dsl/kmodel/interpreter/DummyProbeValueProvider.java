package org.palladiosimulator.simexp.dsl.kmodel.interpreter;

import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Probe;

public class DummyProbeValueProvider implements ProbeValueProvider {
    
    
    
//    protected Double retrieveResponseTime(SelfAdaptiveSystemState<?> sasState) {
//        SimulatedMeasurement simMeasurement = sasState.getQuantifiedState()
//            .findMeasurementWith(responseTimeSpec)
//            .orElseThrow();
//        return simMeasurement.getValue();
    

    @Override
    public Object getValue(Probe probe) {
        // TODO: lookup current measured responseTime from probe
        double currentMeasurementValue = 2.1;
        return currentMeasurementValue;
    }

}
