package org.palladiosimulator.simexp.dsl.kmodel.interpreter;

import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Optimizable;

public interface VariableValueProvider {
	public Object getValue(Optimizable variable);
}
