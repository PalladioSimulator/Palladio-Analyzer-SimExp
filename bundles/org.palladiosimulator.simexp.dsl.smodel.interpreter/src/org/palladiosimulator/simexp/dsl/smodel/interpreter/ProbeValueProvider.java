package org.palladiosimulator.simexp.dsl.smodel.interpreter;

import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;

public interface ProbeValueProvider {
    double getDoubleValue(Probe probe);

    boolean getBooleanValue(Probe probe);
}
