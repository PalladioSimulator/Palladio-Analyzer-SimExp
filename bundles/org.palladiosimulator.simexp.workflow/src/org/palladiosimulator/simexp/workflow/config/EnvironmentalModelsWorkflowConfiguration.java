package org.palladiosimulator.simexp.workflow.config;

public class EnvironmentalModelsWorkflowConfiguration {
	private final String staticModelFile;
	private final String dynamicModelFile;
	
	public EnvironmentalModelsWorkflowConfiguration(final String staticModelFile, final String dynamicModelFile) {
		this.staticModelFile = staticModelFile;
		this.dynamicModelFile = dynamicModelFile;
	}
	
	public String getStaticModelFile() {
		return staticModelFile;
	}
	
	public String getDynamicModelFile() {
		return dynamicModelFile;
	}
}
