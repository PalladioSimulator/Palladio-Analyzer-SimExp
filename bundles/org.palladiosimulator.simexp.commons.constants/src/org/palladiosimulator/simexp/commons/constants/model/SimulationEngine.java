package org.palladiosimulator.simexp.commons.constants.model;

public enum SimulationEngine {
    PCM("PCM"), //
    PRISM("Prism");

    private final String name;

    private SimulationEngine(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static SimulationEngine fromName(String name) {
        for (SimulationEngine engine : SimulationEngine.values()) {
            if (engine.getName()
                .equals(name)) {
                return engine;
            }
        }
        throw new RuntimeException(String.format("Invalid simulation engine: %s", name));
    }
}
