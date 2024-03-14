package org.palladiosimulator.simexp.dsl.kmodel.interpreter;

import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

public interface VariableValueProvider {
    public Object getValue(Optimizable variable);
}
