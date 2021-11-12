package org.palladiosimulator.pcm.query;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;

public class ResourceEnvironmentModelLookup {
    
    
    public ResourceContainer findResourceContainerById(ResourceEnvironment resourceEnv, String resourceContainerId){
        
        EList<ResourceContainer> resourceContainers = resourceEnv.getResourceContainer_ResourceEnvironment();
        for (ResourceContainer resourceContainer : resourceContainers) {
            if(resourceContainer.getId().equals(resourceContainerId)) {
                return resourceContainer;
            }
        }
        return null;
    }

    
    public ResourceContainer findResourceContainerByEntityName(ResourceEnvironment resourceEnv, String resourceContainerEntityName){
        
        EList<ResourceContainer> resourceContainers = resourceEnv.getResourceContainer_ResourceEnvironment();
        for (ResourceContainer resourceContainer : resourceContainers) {
            if(resourceContainer.getEntityName().equals(resourceContainerEntityName)) {
                return resourceContainer;
            }
        }
        return null;
    }

}
