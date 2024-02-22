package org.palladiosimulator.simexp.core.state;

import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.entity.StringRepresentable;

public abstract class ArchitecturalConfiguration<C, A> implements StringRepresentable {

    private C configuration;

    public ArchitecturalConfiguration(C configuration) {
        this.configuration = configuration;
    }

    public C getConfiguration() {
        return configuration;
    }

    public void setConfiguration(C configuration) {
        this.configuration = configuration;
    }

    public abstract String difference(ArchitecturalConfiguration<C, A> other);

    public abstract ArchitecturalConfiguration<C, A> apply(Reconfiguration<A> reconf);

    @Override
    public String toString() {
        return getStringRepresentation();
    }

}
