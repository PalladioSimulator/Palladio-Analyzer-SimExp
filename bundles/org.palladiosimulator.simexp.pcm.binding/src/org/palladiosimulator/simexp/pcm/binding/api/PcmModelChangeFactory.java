package org.palladiosimulator.simexp.pcm.binding.api;

import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.simexp.pcm.binding.resourceenvironment.ResourceContainerPcmModelChange;
import org.palladiosimulator.simexp.pcm.perceiption.PcmModelChange;

public class PcmModelChangeFactory {

    
    public static PcmModelChange createResourceContainerPcmModelChange(String attributeName, PCMResourceSetPartition pcm) {
        return new ResourceContainerPcmModelChange(attributeName, pcm);
    }
}
