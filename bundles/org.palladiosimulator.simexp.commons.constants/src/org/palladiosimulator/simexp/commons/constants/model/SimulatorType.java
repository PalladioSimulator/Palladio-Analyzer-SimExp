package org.palladiosimulator.simexp.commons.constants.model;

public enum SimulatorType {
    CUSTOM("Custom"), //
    MODELLED("Modelled");

    private final String name;

    private SimulatorType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static SimulatorType fromName(String name) {
        for (SimulatorType engine : SimulatorType.values()) {
            if (engine.getName()
                .equals(name)) {
                return engine;
            }
        }
        throw new RuntimeException(String.format("Invalid simulator type: %s", name));
    }
}
