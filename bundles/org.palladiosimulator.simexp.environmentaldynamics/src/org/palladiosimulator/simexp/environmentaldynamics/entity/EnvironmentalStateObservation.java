package org.palladiosimulator.simexp.environmentaldynamics.entity;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.ObservationImpl;

public class EnvironmentalStateObservation<S, V> extends ObservationImpl<PerceivedValue<V>>
        implements PerceivableEnvironmentalState {

    private final EnvironmentalState<S> hiddenState;

    private EnvironmentalStateObservation(PerceivedValue<V> value, EnvironmentalState<S> hiddenState) {
        this.value = value;
        this.hiddenState = hiddenState;
    }

    public static <S, V> EnvironmentalStateObservation<S, V> of(PerceivedValue<V> value,
            EnvironmentalState<S> hiddenState) {
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

    public EnvironmentalState<S> getHiddenState() {
        return hiddenState;
    }

    @Override
    public boolean isInitial() {
        return hiddenState.isInitial();
    }

}
