package org.palladiosimulator.simexp.pcm.binding.resourceenvironment;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.pcm.resourceenvironment.ProcessingResourceSpecification;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.simexp.pcm.util.ExperimentProvider;

import tools.mdsd.probdist.api.entity.CategoricalValue;

/**
 * 
 * Maps server node failures rates as defined in the environmental dynamics model to the pcm architectural model
 * 
 * */
public class ResourceContainerPcmModelChange extends AbstractPcmModelChange {
    
    public ResourceContainerPcmModelChange(String attributeName) {
        super(attributeName);
    }
    
    @Override
    void applyChange(CategoricalValue value) {
        double serverNodeFailureRate = (StringUtils.equals("unavailable", value.get())) ? 1.0 : 0.0;
        
        /**
         * note: The current implementation assumes that, for each trajectory, a new experiment runner is created,
         * i.e. if a new experiment is started, the pcm model must be resetted to the original state; thus
         * we can not pass the experiment runner as constructor param, because it will be continuously updated
         * during the various experiment runs; if we would pass is as constructor param, we would not be able
         * to get the latest updated state of the pcm model: Intead we would always work on a stale state of
         * the pcm model
         * */
        PCMResourceSetPartition pcm = ExperimentProvider.get().getExperimentRunner().getWorkingPartition();
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
