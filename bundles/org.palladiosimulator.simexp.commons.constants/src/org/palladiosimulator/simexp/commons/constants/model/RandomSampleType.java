package org.palladiosimulator.simexp.commons.constants.model;

public enum RandomSampleType {
    COUNT("Count"), //
    RATIO("Ratio");

    private final String name;

    private RandomSampleType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static RandomSampleType fromName(String name) {
        for (RandomSampleType sampleType : RandomSampleType.values()) {
            if (sampleType.getName()
                .equals(name)) {
                return sampleType;
            }
        }
        throw new RuntimeException(String.format("Invalid sample type: %s", name));
    }
}
