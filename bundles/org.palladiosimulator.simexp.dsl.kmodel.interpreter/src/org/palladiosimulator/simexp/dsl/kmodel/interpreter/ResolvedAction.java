package org.palladiosimulator.simexp.dsl.kmodel.interpreter;

import java.util.Map;

import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Action;

public class ResolvedAction {
	private final Action action;
	private final Map<String, Object> arguments;
	
	public ResolvedAction(Action action, Map<String, Object> arguments) {
		this.action = action;
		this.arguments = arguments;
	}
	
	public Action getAction() {
		return this.action;
	}
	
	public Map<String, Object> getArguments() {
		return this.arguments;
	}
	
	public Object getArgument(String name) {
		return this.arguments.get(name);
	}
}