package org.palladiosimulator.simexp.pcm.prism.entity;

import java.io.File;

import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;

public class PrismSimulatedMeasurementSpec extends SimulatedMeasurementSpecification {
	private final File moduleFile;
	private final File propertyFile;

	public PrismSimulatedMeasurementSpec(File moduleFile, File propertyFile) {
		super(propertyFile.getName(), propertyFile.getName());
		
		this.moduleFile = moduleFile;
		this.propertyFile = propertyFile;
	}

	public File getModuleFile() {
		return moduleFile;
	}

	public File getPropertyFile() {
		return propertyFile;
	}
}
