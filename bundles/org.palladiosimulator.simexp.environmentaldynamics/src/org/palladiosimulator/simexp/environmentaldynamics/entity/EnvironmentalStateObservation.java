package org.palladiosimulator.simexp.environmentaldynamics.entity;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.ObservationImpl;

public class EnvironmentalStateObservation<V> extends ObservationImpl implements PerceivableEnvironmentalState {

    private final PerceivedValue<V> value;
    private final EnvironmentalState hiddenState;

    private EnvironmentalStateObservation(PerceivedValue<V> value, EnvironmentalState hiddenState) {
        this.value = value;
        this.hiddenState = hiddenState;
    }

    public static <V> EnvironmentalStateObservation<V> of(PerceivedValue<V> value, EnvironmentalState hiddenState) {
        return new EnvironmentalStateObservation<>(value, hiddenState);
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public PerceivedValue<V> getValue() {
        return value;
    }

    public EnvironmentalState getHiddenState() {
        return hiddenState;
    }

    @Override
    public boolean isInitial() {
        return hiddenState.isInitial();
    }

}
