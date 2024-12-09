package org.palladiosimulator.simexp.commons.constants.model;

public enum ModelledOptimizationType {
    SIMPLE("Simple"), //
    EVOLUTIONARY_ALGORITHM("Evolutionary Algorithm");

    private final String name;

    private ModelledOptimizationType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ModelledOptimizationType fromName(String name) {
        for (ModelledOptimizationType engine : ModelledOptimizationType.values()) {
            if (engine.getName()
                .equals(name)) {
                return engine;
            }
        }
        throw new RuntimeException(String.format("Invalid modelled optimization type: %s", name));
    }
}
