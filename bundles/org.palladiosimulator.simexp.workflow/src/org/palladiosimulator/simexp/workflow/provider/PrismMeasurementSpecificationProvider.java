package org.palladiosimulator.simexp.workflow.provider;

import java.io.File;

import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;

public class PrismMeasurementSpecificationProvider {
	
	public PrismSimulatedMeasurementSpec getSpecification(URI moduleUri, URI propertyUri) {
		String propertyFileAsString = CommonPlugin.resolve(propertyUri).toFileString();
		File propertyFile = new File(propertyFileAsString);
		String moduleFileAsString = CommonPlugin.resolve(moduleUri).toFileString();
		File moduleFile = new File(moduleFileAsString);
		
		return new PrismSimulatedMeasurementSpec(moduleFile, propertyFile);
	}
}
