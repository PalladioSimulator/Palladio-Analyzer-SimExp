package org.palladiosimulator.simexp.dsl.smodel.interpreter.mape;

import java.util.List;

import org.palladiosimulator.simexp.dsl.smodel.interpreter.ResolvedAction;

public interface Executer {
	public void execute(List<ResolvedAction> actions);
}
