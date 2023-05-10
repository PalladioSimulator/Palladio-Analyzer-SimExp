package org.palladiosimulator.simexp.workflow.config;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.analyzer.workflow.configurations.AbstractPCMWorkflowRunConfiguration;
import org.palladiosimulator.simexp.pcm.util.SimulationParameterConfiguration;

public class SimExpWorkflowConfiguration extends AbstractPCMWorkflowRunConfiguration {

    /**
     * This class serves as container configuration class to hold all relevant configuration
     * information
     * 
     */
	private final String simulationEngine;
	private final String qualityObjective;
    private final URI experimentsFile;
    private final URI staticModelFile;
    private final URI dynamicModelFile;
    private final URI monitorRepositoryFile;
    private final List<URI> propertyFiles;
    private final List<URI> moduleFiles;
    private final List<String> monitorNames;
    private final SimulationParameterConfiguration simulationParameters;

    public SimExpWorkflowConfiguration(String simulationEngine, String qualityObjective, 
    		ArchitecturalModelsWorkflowConfiguration architecturalModels,
    		MonitorConfiguration monitors,
    		PrismConfiguration prismConfiguration,
    		EnvironmentalModelsWorkflowConfiguration environmentalModels,
    		SimulationParameterConfiguration simulationParameters) {

        /**
         * workaround: allocation files are required by the parent class
         * AbstractPCMWorkflowRunConfiguration.validateAndFreeze when loading PCMModels; this
         * existence check for PCM models should be done during configuration validation !!! needs
         * refactoring for performability analysis it is current not required; therefore pass empty
         * list in order to successfully execute workflow
         */
    	this.simulationEngine = simulationEngine;
    	this.qualityObjective = qualityObjective;
        this.setUsageModelFile(architecturalModels.getUsageModelFile());
        this.setAllocationFiles(architecturalModels.getAllocationFiles());
        this.experimentsFile = URI.createURI(architecturalModels.getExperimentsFile());
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

    public String getSimulationEngine() {
		return simulationEngine;
	}
    
    public String getQualityObjective() {
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
    
    public SimulationParameterConfiguration getSimulationParameters() {
		return simulationParameters;
	}
}
