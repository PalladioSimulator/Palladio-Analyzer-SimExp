package org.palladiosimulator.simexp.pcm.binding.api;

import org.palladiosimulator.simexp.pcm.binding.resourceenvironment.ResourceContainerPcmModelChange;
import org.palladiosimulator.simexp.pcm.perceiption.PcmModelChange;
import org.palladiosimulator.simexp.pcm.util.ExperimentRunner;

public class PcmModelChangeFactory {

    
    public static PcmModelChange createResourceContainerPcmModelChange(String attributeName, ExperimentRunner experimentRunner) {
        return new ResourceContainerPcmModelChange(attributeName, experimentRunner);
    }
}
