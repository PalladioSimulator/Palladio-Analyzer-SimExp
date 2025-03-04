package org.palladiosimulator.simexp.dsl.ea.api.preferences;

public enum EADispatcherType {
    LOCAl("Local"), //
    KUBERNETES("Kubernetes");

    private final String label;

    private EADispatcherType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
