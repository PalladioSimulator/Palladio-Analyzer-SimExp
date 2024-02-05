package org.palladiosimulator.simexp.environmentaldynamics.entity;

import java.util.Optional;

public interface PerceivedElement<V> extends PerceivedValue<V> {
    Optional<V> getElement(String key);

}
