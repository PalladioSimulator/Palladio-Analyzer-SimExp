package org.palladiosimulator.simexp.dsl.kmodel.interpreter;

import java.util.Map;

public class ResolvedAction {
	private final String name;
	private final Map<String, Object> arguments;
	
	public ResolvedAction(String name, Map<String, Object> arguments) {
		this.name = name;
		this.arguments = arguments;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Map<String, Object> getArguments() {
		return this.arguments;
	}
	
	public Object getArgument(String name) {
		return this.arguments.get(name);
	}
}
