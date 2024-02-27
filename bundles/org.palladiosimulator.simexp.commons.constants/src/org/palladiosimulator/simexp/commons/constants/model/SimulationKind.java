package org.palladiosimulator.simexp.commons.constants.model;

import java.util.Arrays;
import java.util.List;

public enum SimulationKind {
    PERFORMANCE("Performance", Arrays.asList(QualityObjective.PERFORMANCE)), //
    RELIABILITY("Reliability", Arrays.asList(QualityObjective.RELIABILITY)), //
    PERFORMABILITY("Performability", Arrays.asList(QualityObjective.PERFORMABILITY)), //
    MODELLED("Modelled",
            Arrays.asList(QualityObjective.PERFORMANCE, QualityObjective.RELIABILITY, QualityObjective.PERFORMABILITY));

    private final String name;
    private final List<QualityObjective> validObjectives;

    private SimulationKind(String name, List<QualityObjective> validObjectives) {
        this.name = name;
        this.validObjectives = validObjectives;
    }

    public String getName() {
        return name;
    }

    public List<QualityObjective> getValidObjectives() {
        return validObjectives;
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
