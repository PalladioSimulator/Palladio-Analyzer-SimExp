package org.palladiosimulator.simexp.workflow.config;

import java.util.ArrayList;
import java.util.List;

public class ArchitecturalModelsWorkflowConfiguration {
	
    private final List<String> allocationFiles;
    private final String usageModelFile;
	// Simulizar intergration
	private final String monitorRepositoryFile;
	private final String experimentsFile;
	private final List<String> monitors;
	
	public ArchitecturalModelsWorkflowConfiguration(final List<String> allocationFiles, 
			final String usageModelFile, final String monitorRepositoryFile, final String experimentsFile,
			final List<String> monitors) {
		
		this.allocationFiles = new ArrayList<String>();
		this.allocationFiles.addAll(allocationFiles);
		this.usageModelFile = usageModelFile;
		this.monitorRepositoryFile = monitorRepositoryFile;
		this.experimentsFile = experimentsFile;
		this.monitors = monitors;
	}

	public List<String> getAllocationFiles() {
		return List.copyOf(allocationFiles);
	}

	public String getUsageModelFile() {
		return usageModelFile;
	}
	
	public String getMonitorRepositoryFile() {
		return monitorRepositoryFile;
	}
	
	public String getExperimentsFile() {
		return experimentsFile;
	}
	
	public List<String> getMonitors() {
		return List.copyOf(monitors);
	}
}
