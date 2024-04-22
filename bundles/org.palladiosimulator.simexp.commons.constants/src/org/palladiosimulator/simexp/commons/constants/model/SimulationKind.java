package org.palladiosimulator.simexp.commons.constants.model;

public enum SimulationKind {
    PERFORMANCE("Performance"), //
    RELIABILITY("Reliability"), //
    PERFORMABILITY("Performability");

    private final String name;

    private SimulationKind(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static SimulationKind fromName(String name) {
        for (SimulationKind kind : SimulationKind.values()) {
            if (kind.getName()
                .equals(name)) {
                return kind;
            }
        }
        throw new RuntimeException(String.format("Invalid kind: %s", name));
    }
}
