package org.palladiosimulator.simexp.dsl.kmodel.interpreter;

import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;

public interface ProbeValueProvider {
    public Object getValue(Probe probe);
}
