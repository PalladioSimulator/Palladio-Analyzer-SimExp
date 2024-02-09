package org.palladiosimulator.simexp.pcm.perceiption;

import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedValue;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public interface PerceivedValueConverter<V> {
    CategoricalValue convertElement(PerceivedValue<V> change, String key);
}
