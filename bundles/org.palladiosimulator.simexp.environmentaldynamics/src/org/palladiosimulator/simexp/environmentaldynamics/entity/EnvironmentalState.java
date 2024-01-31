package org.palladiosimulator.simexp.environmentaldynamics.entity;

import static java.util.Objects.requireNonNull;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.StateImpl;

public class EnvironmentalState<S, V> extends StateImpl<S> implements PerceivableEnvironmentalState {

    public static class EnvironmentalStateBuilder<S, V> {
        private PerceivedValue<V> value = null;
        private boolean isInitial = false;
        private boolean isHidden = false;

        public EnvironmentalStateBuilder<S, V> withValue(PerceivedValue<V> value) {
            this.value = value;
            return this;
        }

        public EnvironmentalStateBuilder<S, V> isInital() {
            this.isInitial = true;
            return this;
        }

        public EnvironmentalStateBuilder<S, V> isHidden() {
            this.isHidden = true;
            return this;
        }

        public EnvironmentalState<S, V> build() {
            requireNonNull(value, "The environmental state value must not be null");

            return new EnvironmentalState<>(value, isInitial, isHidden);
        }

    }

    private final PerceivedValue<V> value;
    private final boolean isInitial;
    private final boolean isHidden;

    protected EnvironmentalState(PerceivedValue<V> value, boolean isInitial, boolean isHidden) {
        this.value = value;
        this.isInitial = isInitial;
        this.isHidden = isHidden;
    }

    public static <S, V> EnvironmentalStateBuilder<S, V> newBuilder() {
        return new EnvironmentalStateBuilder<>();
    }

    @Override
    public boolean isHidden() {
        return isHidden;
    }

    @Override
    public PerceivedValue<V> getValue() {
        return value;
    }

    @Override
    public boolean isInitial() {
        return isInitial;
    }

}
