package org.palladiosimulator.simexp.commons.constants.model;

public enum QualityObjective {
    PERFORMANCE("Performance"), //
    RELIABILITY("Reliability"), //
    PERFORMABILITY("Performability");

    private final String name;

    private QualityObjective(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static QualityObjective fromName(String name) {
        for (QualityObjective kind : QualityObjective.values()) {
            if (kind.getName()
                .equals(name)) {
                return kind;
            }
        }
        throw new RuntimeException(String.format("Invalid QualityObjective: %s", name));
    }
}
