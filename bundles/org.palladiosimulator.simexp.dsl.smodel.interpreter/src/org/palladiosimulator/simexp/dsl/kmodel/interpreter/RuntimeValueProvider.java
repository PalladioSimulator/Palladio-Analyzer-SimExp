package org.palladiosimulator.simexp.dsl.kmodel.interpreter;

import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Runtime;

public interface RuntimeValueProvider {
	public Object getValue(Runtime runtime);
}
