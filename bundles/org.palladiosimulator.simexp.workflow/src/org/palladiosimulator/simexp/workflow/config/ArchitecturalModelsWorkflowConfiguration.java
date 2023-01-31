package org.palladiosimulator.simexp.workflow.config;

import java.util.ArrayList;
import java.util.List;

public class ArchitecturalModelsWorkflowConfiguration {
	
    private List<String> allocationFiles;
    private String usageModelFile;
	// Simulizar intergration
	private String monitorRepositoryFile;
	private String experimentsFile;
	
	public ArchitecturalModelsWorkflowConfiguration(final List<String> allocationFiles, 
			final String usageModelFile, final String monitorRepositoryFile, final String experimentsFile) {
		
		this.allocationFiles = new ArrayList<String>();
		this.allocationFiles.addAll(allocationFiles);
		this.usageModelFile = usageModelFile;
		this.monitorRepositoryFile = monitorRepositoryFile;
		this.experimentsFile = experimentsFile;
	}

	public List<String> getAllocationFiles() {
		 List<String> copyAllocationFiles = new ArrayList<String>();
		 copyAllocationFiles.addAll(allocationFiles);
		 return copyAllocationFiles;
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
}
