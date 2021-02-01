package org.palladiosimulator.simexp.pcm.binding.resourceenvironment;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.pcm.resourceenvironment.ProcessingResourceSpecification;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

/**
 * 
 * Maps server node failures rates as defined in the environmental dynamics model to the pcm architectural model
 * 
 * */
public class ResourceContainerPcmModelChange extends AbstractPcmModelChange {
    
    private static final Logger LOGGER = Logger.getLogger(ResourceContainerPcmModelChange.class);
    

    public ResourceContainerPcmModelChange(String attributeName, PCMResourceSetPartition pcm) {
        super(attributeName, pcm);
    }
    
    @Override
    void applyChange(Object object) {
        // fixme: try to avoid this case
        double serverNodeFailureRate = (Double) object;
        
        EList<ResourceContainer> resourceContainers = pcm.getResourceEnvironment().getResourceContainer_ResourceEnvironment();
        
        for (ResourceContainer resourceContainer : resourceContainers) {
            EList<ProcessingResourceSpecification> activeResourceSpecs = resourceContainer.getActiveResourceSpecifications_ResourceContainer();
            for (ProcessingResourceSpecification activeResourceSpec : activeResourceSpecs) {
                activeResourceSpec.setMTTF(serverNodeFailureRate);
            }
        }
    }
    
}
