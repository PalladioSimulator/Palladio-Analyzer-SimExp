package org.palladiosimulator.simexp.dsl.smodel.interpreter;

import org.palladiosimulator.simexp.dsl.smodel.smodel.Runtime;

public interface RuntimeValueProvider {
    public Object getValue(Runtime runtime);
}