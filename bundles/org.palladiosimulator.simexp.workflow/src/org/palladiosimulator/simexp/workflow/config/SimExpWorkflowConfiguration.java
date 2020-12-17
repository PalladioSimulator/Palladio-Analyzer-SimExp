package org.palladiosimulator.simexp.workflow.config;

import org.palladiosimulator.analyzer.workflow.configurations.AbstractPCMWorkflowRunConfiguration;

public class SimExpWorkflowConfiguration extends AbstractPCMWorkflowRunConfiguration {
    
    /**
     * This class serves as container configuration class to hold all relevant configuration informationa
     * 
     * */
    
    public SimExpWorkflowConfiguration(ArchitecturalModelsWorkflowConfiguration inputModels) {
        
        /**
         * workaround: 
         * allocation files are required by the parent class AbstractPCMWorkflowRunConfiguration.validateAndFreeze when loading PCMModels;
         * this existence check for PCM models should be done during configuration validation !!! needs refactoring
         * for performability analysis it is current not required; therefore pass empty list in order to successfully execute workflow
         * */
        this.setUsageModelFile(inputModels.getUsageModelFile());
        this.setAllocationFiles(inputModels.getAllocationFiles());
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

}
