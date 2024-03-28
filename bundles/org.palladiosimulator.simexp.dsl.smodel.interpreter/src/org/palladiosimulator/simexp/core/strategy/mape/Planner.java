package org.palladiosimulator.simexp.core.strategy.mape;

import java.util.List;

import org.palladiosimulator.simexp.dsl.smodel.interpreter.ResolvedAction;

public interface Planner {
	public List<ResolvedAction> plan();
}
