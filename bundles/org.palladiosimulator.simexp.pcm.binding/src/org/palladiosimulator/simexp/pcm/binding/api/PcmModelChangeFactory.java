package org.palladiosimulator.simexp.pcm.binding.api;

import org.palladiosimulator.simexp.pcm.binding.resourceenvironment.ResourceContainerPcmModelChange;
import org.palladiosimulator.simexp.pcm.perceiption.PcmModelChange;
import org.palladiosimulator.simexp.pcm.perceiption.PerceivedValueConverter;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;

public class PcmModelChangeFactory {

    public static <V> PcmModelChange<V> createResourceContainerPcmModelChange(String attributeName,
            PerceivedValueConverter<V> perceivedValueConverter, IExperimentProvider experimentProvider) {
        return new ResourceContainerPcmModelChange<>(attributeName, perceivedValueConverter, experimentProvider);
    }
}
