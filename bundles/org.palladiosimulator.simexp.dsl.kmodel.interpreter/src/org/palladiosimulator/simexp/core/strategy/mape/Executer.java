package org.palladiosimulator.simexp.core.strategy.mape;

import java.util.List;

import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Action;

public interface Executer {
	public void execute(List<Action> actions);
}
