package org.palladiosimulator.simexp.dsl.kmodel.interpreter.mocks;

import org.palladiosimulator.simexp.dsl.kmodel.interpreter.ProbeValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;

public class TestProbeValueProvider implements ProbeValueProvider {

    @Override
    public Object getValue(Probe probe) {
        switch (probe.getDataType()) {
        case BOOL:
            return true;

        case INT:
            return 1;

        case FLOAT:
            return 0.12345;

        case STRING:
            return "string";

        default:
            return null;
        }
    }

}
