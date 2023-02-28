package org.palladiosimulator.simexp.workflow.config;

import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.analyzer.workflow.configurations.AbstractPCMWorkflowRunConfiguration;

public class SimExpWorkflowConfiguration extends AbstractPCMWorkflowRunConfiguration {

    /**
     * This class serves as container configuration class to hold all relevant configuration
     * information
     * 
     */
    private final URI experimentsFile;
    private final URI staticModelFile;
    private final URI dynamicModelFile;
    private final SimulationParameterConfiguration simulationParameters;

    public SimExpWorkflowConfiguration(ArchitecturalModelsWorkflowConfiguration architecturalModels,
    		EnvironmentalModelsWorkflowConfiguration environmentalModels,
    		SimulationParameterConfiguration simulationParameters) {

        /**
         * workaround: allocation files are required by the parent class
         * AbstractPCMWorkflowRunConfiguration.validateAndFreeze when loading PCMModels; this
         * existence check for PCM models should be done during configuration validation !!! needs
         * refactoring for performability analysis it is current not required; therefore pass empty
         * list in order to successfully execute workflow
         */
        this.setUsageModelFile(architecturalModels.getUsageModelFile());
        this.setAllocationFiles(architecturalModels.getAllocationFiles());
        this.experimentsFile = URI.createURI(architecturalModels.getExperimentsFile());
        this.staticModelFile = URI.createURI(environmentalModels.getStaticModelFile());
        this.dynamicModelFile = URI.createURI(environmentalModels.getDynamicModelFile());
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

    
    public URI getExperimentsURI() {
        return experimentsFile;
    }
    
    public URI getStaticModelURI() {
    	return staticModelFile;
    }
    
    public URI getDynamicModelURI() {
    	return dynamicModelFile;
    }
    
    public SimulationParameterConfiguration getSimulationParameters() {
		return simulationParameters;
	}
}
