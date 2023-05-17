package org.palladiosimulator.simexp.workflow.config;

import java.util.ArrayList;
import java.util.List;

public class ArchitecturalModelsWorkflowConfiguration {
	
    private final List<String> allocationFiles;
    private final String usageModelFile;
	// Simulizar intergration
    private String kmodelFile;
	private final String experimentsFile;

	// FIXME: check constructor call for kmodel file
	public ArchitecturalModelsWorkflowConfiguration(final List<String> allocationFiles, final String usageModelFile, final String experimentsFile, String kmodelFile) {
		this.allocationFiles = new ArrayList<String>();
		this.allocationFiles.addAll(allocationFiles);
		this.usageModelFile = usageModelFile;
		this.experimentsFile = experimentsFile;
		this.kmodelFile = kmodelFile;
	}

	public List<String> getAllocationFiles() {
		return List.copyOf(allocationFiles);
	}

	public String getUsageModelFile() {
		return usageModelFile;
	}
	
	public String getKmodelFile(){
	    return kmodelFile;
	}
	
	public String getExperimentsFile() {
		return experimentsFile;
	}
}
