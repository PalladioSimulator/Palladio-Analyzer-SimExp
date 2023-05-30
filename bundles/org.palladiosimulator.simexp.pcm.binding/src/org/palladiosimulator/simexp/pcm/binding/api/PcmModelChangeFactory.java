package org.palladiosimulator.simexp.pcm.binding.api;

import org.palladiosimulator.simexp.pcm.binding.resourceenvironment.ResourceContainerPcmModelChange;
import org.palladiosimulator.simexp.pcm.perceiption.PcmModelChange;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;

public class PcmModelChangeFactory {

    
    public static PcmModelChange createResourceContainerPcmModelChange(String attributeName, IExperimentProvider experimentProvider) {
        return new ResourceContainerPcmModelChange(attributeName, experimentProvider);
    }
}
