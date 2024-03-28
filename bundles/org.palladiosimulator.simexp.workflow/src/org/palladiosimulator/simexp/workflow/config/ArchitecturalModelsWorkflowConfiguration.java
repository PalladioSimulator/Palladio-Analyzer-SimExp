package org.palladiosimulator.simexp.workflow.config;

import java.util.ArrayList;
import java.util.List;

public class ArchitecturalModelsWorkflowConfiguration {

    private final List<String> allocationFiles;
    private final String usageModelFile;
    // Simulizar intergration
    private String smodelFile;
    private final String experimentsFile;

    // FIXME: check constructor call for smodel file
    public ArchitecturalModelsWorkflowConfiguration(final List<String> allocationFiles, final String usageModelFile,
            final String experimentsFile, String smodelFile) {
        this.allocationFiles = new ArrayList<>();
        this.allocationFiles.addAll(allocationFiles);
        this.usageModelFile = usageModelFile;
        this.experimentsFile = experimentsFile;
        this.smodelFile = smodelFile;
    }

    public List<String> getAllocationFiles() {
        return List.copyOf(allocationFiles);
    }

    public String getUsageModelFile() {
        return usageModelFile;
    }

    public String getSmodelFile() {
        return smodelFile;
    }

    public String getExperimentsFile() {
        return experimentsFile;
    }
}
