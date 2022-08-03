package org.palladiosimulator.simexp.dsl.kmodel.interpreter;

import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Variable;

public interface VariableValueProvider {
	public Object getValue(Variable variable);
}
