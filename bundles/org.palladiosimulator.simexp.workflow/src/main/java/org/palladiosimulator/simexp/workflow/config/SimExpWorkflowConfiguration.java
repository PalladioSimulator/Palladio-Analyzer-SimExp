package org.palladiosimulator.simexp.workflow.config;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.analyzer.workflow.configurations.AbstractPCMWorkflowRunConfiguration;
import org.palladiosimulator.simexp.commons.constants.model.QualityObjective;
import org.palladiosimulator.simexp.commons.constants.model.SimulationEngine;
import org.palladiosimulator.simexp.commons.constants.model.SimulatorType;
import org.palladiosimulator.simexp.pcm.util.SimulationParameters;

public class SimExpWorkflowConfiguration extends AbstractPCMWorkflowRunConfiguration {

    /**
     * This class serves as container configuration class to hold all relevant configuration
     * information
     * 
     */
    private final URI smodelFile;
    private final SimulatorType simulatorType;
    private final SimulationEngine simulationEngine;
    private final QualityObjective qualityObjective;
    private final URI experimentsFile;
    private final URI staticModelFile;
    private final URI dynamicModelFile;
    private final URI monitorRepositoryFile;
    private final List<URI> propertyFiles;
    private final List<URI> moduleFiles;
    private final List<String> monitorNames;
    private final SimulationParameters simulationParameters;

    public SimExpWorkflowConfiguration(SimulatorType simulatorType, SimulationEngine simulationEngine,
            QualityObjective qualityObjective, ArchitecturalModelsWorkflowConfiguration architecturalModels,
            MonitorConfiguration monitors, PrismConfiguration prismConfiguration,
            EnvironmentalModelsWorkflowConfiguration environmentalModels, SimulationParameters simulationParameters) {

        /**
         * workaround: allocation files are required by the parent class
         * AbstractPCMWorkflowRunConfiguration.validateAndFreeze when loading PCMModels; this
         * existence check for PCM models should be done during configuration validation !!! needs
         * refactoring for performability analysis it is current not required; therefore pass empty
         * list in order to successfully execute workflow
         */
        this.simulatorType = simulatorType;
        this.simulationEngine = simulationEngine;
        this.qualityObjective = qualityObjective;
        this.setUsageModelFile(architecturalModels.getUsageModelFile());
        this.setAllocationFiles(architecturalModels.getAllocationFiles());
        this.experimentsFile = URI.createURI(architecturalModels.getExperimentsFile());
        this.smodelFile = URI.createURI(architecturalModels.getSmodelFile());
        this.staticModelFile = URI.createURI(environmentalModels.getStaticModelFile());
        this.dynamicModelFile = URI.createURI(environmentalModels.getDynamicModelFile());
        this.monitorRepositoryFile = URI.createURI(monitors.getMonitorRepositoryFile());
        this.monitorNames = monitors.getMonitors();

        this.propertyFiles = prismConfiguration.getPropertyFiles()
            .stream()
            .map(URI::createURI)
            .toList();
        this.moduleFiles = prismConfiguration.getModuleFIles()
            .stream()
            .map(URI::createURI)
            .toList();

        this.simulationParameters = simulationParameters;
    }

    @Override
    public String getErrorMessage() {
        // configuration validation is already done in the LaunchConfiguration class
        // currently no error messages available; return null otherwise workflow validation fails
        return null;
    }

    @Override
    public void setDefaults() {
        // FIXME: check what shall be done here
    }

    public URI getSmodelURI() {
        return smodelFile;
    }

    public SimulatorType getSimulatorType() {
        return simulatorType;
    }

    public SimulationEngine getSimulationEngine() {
        return simulationEngine;
    }

    public QualityObjective getQualityObjective() {
        return qualityObjective;
    }

    public URI getExperimentsURI() {
        return experimentsFile;
    }

    public URI getStaticModelURI() {
        return staticModelFile;
    }

    public URI getDynamicModelURI() {
        return dynamicModelFile;
    }

    public URI getMonitorRepositoryURI() {
        return monitorRepositoryFile;
    }

    public List<String> getMonitorNames() {
        return List.copyOf(monitorNames);
    }

    public List<URI> getPropertyFiles() {
        return List.copyOf(propertyFiles);
    }

    public List<URI> getModuleFiles() {
        return List.copyOf(moduleFiles);
    }

    public SimulationParameters getSimulationParameters() {
        return simulationParameters;
    }
}
