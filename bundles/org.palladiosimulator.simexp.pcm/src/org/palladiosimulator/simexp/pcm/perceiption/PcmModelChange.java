package org.palladiosimulator.simexp.pcm.perceiption;

import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedValue;

public interface PcmModelChange<V> {

    public void apply(PerceivedValue<V> change);

}
