package org.palladiosimulator.simexp.commons.constants.model;

public enum RewardType {
    EXPECTED("Expected"), //
    ACCUMULATED("Accumulated");

    private final String name;

    private RewardType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static RewardType fromName(String name) {
        for (RewardType engine : RewardType.values()) {
            if (engine.getName()
                .equals(name)) {
                return engine;
            }
        }
        throw new RuntimeException(String.format("Invalid reward type: %s", name));
    }
}
