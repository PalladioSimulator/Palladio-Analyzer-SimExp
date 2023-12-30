package org.palladiosimulator.simexp.core.state;

import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.entity.StringRepresentable;

public abstract class ArchitecturalConfiguration<S, A> implements StringRepresentable {

    private S configuration;

    public ArchitecturalConfiguration(S configuration) {
        this.configuration = configuration;
    }

    public S getConfiguration() {
        return configuration;
    }

    public void setConfiguration(S configuration) {
        this.configuration = configuration;
    }

    public abstract String difference(ArchitecturalConfiguration<S, A> other);

    public abstract ArchitecturalConfiguration<S, A> apply(Reconfiguration<A> reconf);

    @Override
    public String toString() {
        return getStringRepresentation();
    }

}
