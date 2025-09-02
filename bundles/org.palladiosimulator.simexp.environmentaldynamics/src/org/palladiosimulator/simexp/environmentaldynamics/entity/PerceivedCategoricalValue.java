package org.palladiosimulator.simexp.environmentaldynamics.entity;

import java.util.Optional;

public class PerceivedCategoricalValue implements PerceivedElement<String> {

    private final String name;
    private final String value;

    public PerceivedCategoricalValue(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public Optional<String> getElement(String key) {
        if (name.equals(key)) {
            return Optional.of(value);
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return String.format("%1s: %2s", name, value);
    }

}
