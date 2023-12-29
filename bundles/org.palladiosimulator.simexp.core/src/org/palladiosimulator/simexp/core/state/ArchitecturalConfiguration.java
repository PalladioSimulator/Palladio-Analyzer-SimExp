package org.palladiosimulator.simexp.core.state;

import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.entity.StringRepresentable;

public abstract class ArchitecturalConfiguration<T> implements StringRepresentable {

    private T configuration;

    public ArchitecturalConfiguration(T configuration) {
        this.configuration = configuration;
    }

    public T getConfiguration() {
        return configuration;
    }

    public void setConfiguration(T configuration) {
        this.configuration = configuration;
    }

    public abstract String difference(ArchitecturalConfiguration<?> other);

    public abstract ArchitecturalConfiguration<T> apply(Reconfiguration<T> reconf);

    @Override
    public String toString() {
        return getStringRepresentation();
    }

}
