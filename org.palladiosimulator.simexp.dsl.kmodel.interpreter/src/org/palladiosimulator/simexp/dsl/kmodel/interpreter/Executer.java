package org.palladiosimulator.simexp.dsl.kmodel.interpreter;

import java.util.List;

import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Action;

public interface Executer {
	public void execute(List<Action> actions);
}
