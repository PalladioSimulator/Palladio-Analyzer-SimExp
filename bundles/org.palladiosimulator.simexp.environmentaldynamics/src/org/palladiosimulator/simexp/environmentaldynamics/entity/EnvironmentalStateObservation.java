package org.palladiosimulator.simexp.environmentaldynamics.entity;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.ObservationImpl;

public class EnvironmentalStateObservation<S> extends ObservationImpl<PerceivedValue<?>>
        implements PerceivableEnvironmentalState {

    private final EnvironmentalState<S> hiddenState;

    private EnvironmentalStateObservation(PerceivedValue<?> value, EnvironmentalState<S> hiddenState) {
        this.value = value;
        this.hiddenState = hiddenState;
    }

    public static <S> EnvironmentalStateObservation<S> of(PerceivedValue<?> value, EnvironmentalState<S> hiddenState) {
        return new EnvironmentalStateObservation<>(value, hiddenState);
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public PerceivedValue<?> getValue() {
        return value;
    }

    public EnvironmentalState<S> getHiddenState() {
        return hiddenState;
    }

    @Override
    public boolean isInitial() {
        return hiddenState.isInitial();
    }

}
