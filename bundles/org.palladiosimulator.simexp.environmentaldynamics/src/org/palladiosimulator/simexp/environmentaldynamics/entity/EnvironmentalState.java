package org.palladiosimulator.simexp.environmentaldynamics.entity;

import static java.util.Objects.requireNonNull;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.impl.StateImpl;

public class EnvironmentalState<S> extends StateImpl<S> implements PerceivableEnvironmentalState {

    public static class EnvironmentalStateBuilder<S> {
        private PerceivedValue<?> value = null;
        private boolean isInitial = false;
        private boolean isHidden = false;

        public EnvironmentalStateBuilder<S> withValue(PerceivedValue<?> value) {
            this.value = value;
            return this;
        }

        public EnvironmentalStateBuilder<S> isInital() {
            this.isInitial = true;
            return this;
        }

        public EnvironmentalStateBuilder<S> isHidden() {
            this.isHidden = true;
            return this;
        }

        public EnvironmentalState<S> build() {
            requireNonNull(value, "The environmental state value must not be null");

            return new EnvironmentalState<>(value, isInitial, isHidden);
        }

    }

    private final PerceivedValue<?> value;
    private final boolean isInitial;
    private final boolean isHidden;

    protected EnvironmentalState(PerceivedValue<?> value, boolean isInitial, boolean isHidden) {
        this.value = value;
        this.isInitial = isInitial;
        this.isHidden = isHidden;
    }

    public static <S> EnvironmentalStateBuilder<S> newBuilder() {
        return new EnvironmentalStateBuilder<>();
    }

    @Override
    public boolean isHidden() {
        return isHidden;
    }

    @Override
    public PerceivedValue<?> getValue() {
        return value;
    }

    @Override
    public boolean isInitial() {
        return isInitial;
    }

}
