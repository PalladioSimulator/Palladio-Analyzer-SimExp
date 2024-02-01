package org.palladiosimulator.simexp.environmentaldynamics.entity;

public interface PerceivableEnvironmentalState<V> {

    public boolean isHidden();

    public boolean isInitial();

    public PerceivedValue<V> getValue();

    default public String getStringRepresentation() {
        return getValue().toString();
    }

}
