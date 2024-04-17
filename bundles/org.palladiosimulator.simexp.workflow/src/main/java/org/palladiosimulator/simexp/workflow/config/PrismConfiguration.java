package org.palladiosimulator.simexp.workflow.config;

import java.util.List;

public class PrismConfiguration {
	private final List<String> propertyFiles;
	private final List<String> moduleFIles;
	
	public PrismConfiguration(List<String> propertyFiles, List<String> moduleFiles) {
		this.propertyFiles = propertyFiles;
		this.moduleFIles = moduleFiles;
	}
	
	public List<String> getPropertyFiles() {
		return List.copyOf(propertyFiles);
	}

	public List<String> getModuleFIles() {
		return List.copyOf(moduleFIles);
	}
}
