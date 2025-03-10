package org.palladiosimulator.simexp.workflow.api;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.analyzer.workflow.core.configurations.AbstractPCMWorkflowRunConfiguration;
import org.palladiosimulator.simexp.commons.constants.model.QualityObjective;
import org.palladiosimulator.simexp.commons.constants.model.SimulationEngine;
import org.palladiosimulator.simexp.commons.constants.model.SimulatorType;
import org.palladiosimulator.simexp.pcm.config.IModelledPrismWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.config.IModelledWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.config.IPrismWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.config.SimulationParameters;
import org.palladiosimulator.simexp.pcm.modelled.simulator.config.IModelledPcmWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.simulator.config.IPCMWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.config.ArchitecturalModelsWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.config.EnvironmentalModelsWorkflowConfiguration;
import org.palladiosimulator.simexp.workflow.config.MonitorConfiguration;
import org.palladiosimulator.simexp.workflow.config.PrismConfiguration;

import tools.mdsd.probdist.api.random.ISeedProvider;

public class SimExpWorkflowConfiguration extends AbstractPCMWorkflowRunConfiguration
        implements IPCMWorkflowConfiguration, IPrismWorkflowConfiguration, IModelledWorkflowConfiguration,
        IModelledPcmWorkflowConfiguration, IModelledPrismWorkflowConfiguration {

    /**
     * This class serves as container configuration class to hold all relevant configuration
     * information
     * 
     */
    private final URI smodelFile;
    private final SimulatorType simulatorType;
    private final SimulationEngine simulationEngine;
    private final Set<String> transformationNames;
    private final QualityObjective qualityObjective;
    private final URI experimentsFile;
    private final URI staticModelFile;
    private final URI dynamicModelFile;
    private final URI monitorRepositoryFile;
    private final List<URI> propertyFiles;
    private final List<URI> moduleFiles;
    private final List<String> monitorNames;
    private final SimulationParameters simulationParameters;
    private final Optional<ISeedProvider> seedProvider;

    public SimExpWorkflowConfiguration(SimulatorType simulatorType, SimulationEngine simulationEngine,
            Set<String> transformationNames, QualityObjective qualityObjective,
            ArchitecturalModelsWorkflowConfiguration architecturalModels, MonitorConfiguration monitors,
            PrismConfiguration prismConfiguration, EnvironmentalModelsWorkflowConfiguration environmentalModels,
            SimulationParameters simulationParameters, Optional<ISeedProvider> seedProvider) {

        /**
         * workaround: allocation files are required by the parent class
         * AbstractPCMWorkflowRunConfiguration.validateAndFreeze when loading PCMModels; this
         * existence check for PCM models should be done during configuration validation !!! needs
         * refactoring for performability analysis it is current not required; therefore pass empty
         * list in order to successfully execute workflow
         */
        this.simulatorType = simulatorType;
        this.simulationEngine = simulationEngine;
        this.transformationNames = transformationNames;
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
        this.seedProvider = seedProvider;
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

    @Override
    public URI getSmodelURI() {
        return smodelFile;
    }

    @Override
    public SimulatorType getSimulatorType() {
        return simulatorType;
    }

    @Override
    public SimulationEngine getSimulationEngine() {
        return simulationEngine;
    }

    @Override
    public Set<String> getTransformationNames() {
        return transformationNames;
    }

    @Override
    public QualityObjective getQualityObjective() {
        return qualityObjective;
    }

    @Override
    public URI getExperimentsURI() {
        return experimentsFile;
    }

    @Override
    public URI getStaticModelURI() {
        return staticModelFile;
    }

    @Override
    public URI getDynamicModelURI() {
        return dynamicModelFile;
    }

    public URI getMonitorRepositoryURI() {
        return monitorRepositoryFile;
    }

    @Override
    public List<String> getMonitorNames() {
        return List.copyOf(monitorNames);
    }

    @Override
    public List<URI> getPropertyFiles() {
        return List.copyOf(propertyFiles);
    }

    @Override
    public List<URI> getModuleFiles() {
        return List.copyOf(moduleFiles);
    }

    @Override
    public SimulationParameters getSimulationParameters() {
        return simulationParameters;
    }

    @Override
    public Optional<ISeedProvider> getSeedProvider() {
        return seedProvider;
    }
}
