package org.palladiosimulator.simexp.workflow.config;

import java.util.ArrayList;
import java.util.List;

public class ArchitecturalModelsWorkflowConfiguration {
	
    private List<String> allocationFiles;
    private String usageModelFile;
	// Simulizar intergration
	private String monitorRepositoryFile;
    private String kmodelFile;
	
	public ArchitecturalModelsWorkflowConfiguration(final String usageModelFile, final List<String> allocationFiles, final String monitorRepositoryFile, String kmodelFile) {
		
		this.usageModelFile = usageModelFile;
		this.monitorRepositoryFile = monitorRepositoryFile;
		this.allocationFiles = new ArrayList<String>();
		this.allocationFiles.addAll(allocationFiles);
		this.kmodelFile = kmodelFile;
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
	
	public String getKmodelFile(){
	    return kmodelFile;
	}
	

}
