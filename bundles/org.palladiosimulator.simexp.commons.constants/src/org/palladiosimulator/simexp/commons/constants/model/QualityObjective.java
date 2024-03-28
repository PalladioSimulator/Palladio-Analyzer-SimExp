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
}
