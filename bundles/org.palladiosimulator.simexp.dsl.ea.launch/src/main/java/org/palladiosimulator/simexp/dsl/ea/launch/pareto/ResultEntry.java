package org.palladiosimulator.simexp.dsl.ea.launch.pareto;

import java.util.Map;

class ResultEntry {
    public final double fitness;
    public final Map<String, Object> optimizables;

    public ResultEntry(double fitness, Map<String, Object> optimizables) {
        this.fitness = fitness;
        this.optimizables = optimizables;
    }
}