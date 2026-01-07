package org.palladiosimulator.simexp.dsl.ea.launch.io;

import java.util.Map;

public class ResultEntry {
    public final double fitness;
    public final Map<String, Object> optimizables;
    public final String id;

    public ResultEntry(double fitness, Map<String, Object> optimizables, String id) {
        this.fitness = fitness;
        this.optimizables = optimizables;
        this.id = id;
    }
}