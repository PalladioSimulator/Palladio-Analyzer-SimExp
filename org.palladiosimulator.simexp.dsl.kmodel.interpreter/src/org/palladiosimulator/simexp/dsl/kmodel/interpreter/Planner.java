package org.palladiosimulator.simexp.dsl.kmodel.interpreter;

import java.util.List;

import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Action;

public interface Planner {
	public List<Action> plan();
}
