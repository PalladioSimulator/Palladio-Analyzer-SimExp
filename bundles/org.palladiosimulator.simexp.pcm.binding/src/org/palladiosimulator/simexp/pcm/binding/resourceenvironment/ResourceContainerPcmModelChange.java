package org.palladiosimulator.simexp.pcm.binding.resourceenvironment;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.pcm.resourceenvironment.ProcessingResourceSpecification;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

import tools.mdsd.probdist.api.entity.CategoricalValue;

/**
 * 
 * Maps server node failures rates as defined in the environmental dynamics model to the pcm architectural model
 * 
 * */
public class ResourceContainerPcmModelChange extends AbstractPcmModelChange {
    
    public ResourceContainerPcmModelChange(String attributeName, PCMResourceSetPartition pcm) {
        super(attributeName, pcm);
    }
    
    @Override
    void applyChange(CategoricalValue value) { 
        double serverNodeFailureRate = (StringUtils.equals("unavailable", value.get())) ? 1.0 : 0.0;
        EList<ResourceContainer> resourceContainers = pcm.getResourceEnvironment().getResourceContainer_ResourceEnvironment();
        for (ResourceContainer resourceContainer : resourceContainers) {
            EList<ProcessingResourceSpecification> activeResourceSpecs = resourceContainer.getActiveResourceSpecifications_ResourceContainer();
            for (ProcessingResourceSpecification activeResourceSpec : activeResourceSpecs) {
                activeResourceSpec.setMTTF(serverNodeFailureRate);
                // FIXME: currently we assign the same failure rate both for mttf and mttr; must be modelled independently in the envDyn model later
                activeResourceSpec.setMTTR(serverNodeFailureRate);
            }
        }
    }
    
}
