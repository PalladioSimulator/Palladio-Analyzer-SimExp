package org.palladiosimulator.simexp.workflow.config;

import java.util.ArrayList;
import java.util.List;

public class ArchitecturalModelsWorkflowConfiguration {
	
    private List<String> allocationFiles;
    private String usageModelFile;
	// Simulizar intergration
	private String monitorRepositoryFile;
	
	public ArchitecturalModelsWorkflowConfiguration(final String usageModelFile, final List<String> allocationFiles, final String monitorRepositoryFile) {
		
		this.usageModelFile = usageModelFile;
		this.monitorRepositoryFile = monitorRepositoryFile;
		this.allocationFiles = new ArrayList<String>();
		this.allocationFiles.addAll(allocationFiles);
	}

	public String getMonitorRepositoryFile() {
		return monitorRepositoryFile;
	}

	public List<String> getAllocationFiles() {
		 List<String> copyAllocationFiles = new ArrayList<String>();
		 copyAllocationFiles.addAll(allocationFiles);
		 return copyAllocationFiles;
	}

	public String getUsageModelFile() {
		return usageModelFile;
	}
	

}
