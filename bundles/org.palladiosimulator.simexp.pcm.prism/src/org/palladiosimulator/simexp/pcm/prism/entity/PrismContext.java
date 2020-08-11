package org.palladiosimulator.simexp.pcm.prism.entity;

public class PrismContext {
	
	public String moduleFileContent;
	public final String propertyFileContent;

	public PrismContext(String moduleFileContent, String propertyFileContent) {
		this.moduleFileContent = moduleFileContent;
		this.propertyFileContent = propertyFileContent;
	}
}
