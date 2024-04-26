package org.palladiosimulator.simexp.workflow.provider;

import java.io.File;
import java.util.List;
import java.util.stream.IntStream;

import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.simexp.pcm.config.IPrismConfiguration;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;

public class PrismMeasurementSpecificationProvider {
    private final IPrismConfiguration workflowConfiguration;

    public PrismMeasurementSpecificationProvider(IPrismConfiguration workflowConfiguration) {
        this.workflowConfiguration = workflowConfiguration;
    }

    public List<PrismSimulatedMeasurementSpec> getSpecifications() {
        List<URI> propertyFiles = workflowConfiguration.getPropertyFiles();
        List<URI> moduleFiles = workflowConfiguration.getModuleFiles();
        List<PrismSimulatedMeasurementSpec> prismSpecs = IntStream
            .range(0, Math.min(propertyFiles.size(), moduleFiles.size()))
            .mapToObj(i -> getSpecification(moduleFiles.get(i), propertyFiles.get(i)))
            .toList();
        return prismSpecs;
    }

    private PrismSimulatedMeasurementSpec getSpecification(URI moduleUri, URI propertyUri) {
        String propertyFileAsString = CommonPlugin.resolve(propertyUri)
            .toFileString();
        File propertyFile = new File(propertyFileAsString);
        String moduleFileAsString = CommonPlugin.resolve(moduleUri)
            .toFileString();
        File moduleFile = new File(moduleFileAsString);

        return new PrismSimulatedMeasurementSpec(moduleFile, propertyFile);
    }
}
