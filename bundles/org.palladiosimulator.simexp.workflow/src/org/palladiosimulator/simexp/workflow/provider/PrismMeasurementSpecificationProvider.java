package org.palladiosimulator.simexp.workflow.provider;

import java.io.File;

import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;
//import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;

public class PrismMeasurementSpecificationProvider {
	public PrismSimulatedMeasurementSpec getSpecification(URI propertyUri, URI moduleUri) {
		File propertyFile = new File(CommonPlugin.resolve(propertyUri).toFileString());
		File moduleFile = new File(CommonPlugin.resolve(moduleUri).toFileString());
		
		return new PrismSimulatedMeasurementSpec(propertyFile, moduleFile);
	}
}
