package org.palladiosimulator.simexp.dsl.kmodel.interpreter;

import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Probe;

public interface ProbeValueProvider {
    double getDoubleValue(Probe probe);

    boolean getBooleanValue(Probe probe);
}
