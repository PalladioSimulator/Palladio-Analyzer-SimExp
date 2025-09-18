package org.palladiosimulator.simexp.dsl.ea.launch.pareto;

import java.util.Map;

class ParetoEntry {
    public final double fitness;
    public final Map<String, Object> optimizables;

    public ParetoEntry(double fitness, Map<String, Object> optimizables) {
        this.fitness = fitness;
        this.optimizables = optimizables;
    }
}