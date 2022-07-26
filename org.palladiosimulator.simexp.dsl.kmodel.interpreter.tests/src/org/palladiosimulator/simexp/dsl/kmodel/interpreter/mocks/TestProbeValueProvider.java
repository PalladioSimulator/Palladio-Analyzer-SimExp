package org.palladiosimulator.simexp.dsl.kmodel.interpreter.mocks;

import org.palladiosimulator.simexp.dsl.kmodel.interpreter.ProbeValueProvider;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Probe;

public class TestProbeValueProvider implements ProbeValueProvider {

    @Override
    public Object getValue(Probe probe) {
        double currentMeasurementValue = 0.12345;
        return currentMeasurementValue;
    }

}
