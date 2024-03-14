package org.palladiosimulator.simexp.dsl.smodel.interpreter;

import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;

public interface ProbeValueProvider {
    public Object getValue(Probe probe);
}
