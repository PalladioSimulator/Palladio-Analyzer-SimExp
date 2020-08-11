package org.palladiosimulator.simexp.pcm.prism.entity;

import java.io.File;
import java.util.Objects;

import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;

public class PrismSimulatedMeasurementSpec extends SimulatedMeasurementSpecification {

	public static class PrismSimulatedMeasurementSpecBuilder {

		private String property = "";
		private File moduleFile = null;
		private File propertyFile = null;

		private PrismSimulatedMeasurementSpecBuilder() {
			
		}
		
		public PrismSimulatedMeasurementSpecBuilder withProperty(String property) {
			this.property = property;
			return this;
		}

		public PrismSimulatedMeasurementSpecBuilder andModuleFile(File moduleFile) {
			this.moduleFile = moduleFile;
			return this;
		}

		public PrismSimulatedMeasurementSpecBuilder andPropertyFile(File propertyFile) {
			this.propertyFile = propertyFile;
			return this;
		}

		public PrismSimulatedMeasurementSpec build() {
			checkValidity();

			return new PrismSimulatedMeasurementSpec(property, moduleFile, propertyFile);
		}

		private void checkValidity() {
			if (property == "") {
				// TODO exception hanlding
				throw new RuntimeException("Property must set.");
			}
			Objects.requireNonNull(moduleFile, "Module file must exist.");
			Objects.requireNonNull(propertyFile, "Property file must exist.");
		}

	}

	private final String property;
	private final File moduleFile;
	private final File propertyFile;

	private PrismSimulatedMeasurementSpec(String property, File moduleFile, File propertyFile) {
		super(property, property);
		
		this.property = property;
		this.moduleFile = moduleFile;
		this.propertyFile = propertyFile;
	}

	public static PrismSimulatedMeasurementSpecBuilder newBuilder() {
		return new PrismSimulatedMeasurementSpecBuilder();
	}
	
	public String getProperty() {
		return property;
	}

	public File getModuleFile() {
		return moduleFile;
	}

	public File getPropertyFile() {
		return propertyFile;
	}

}
