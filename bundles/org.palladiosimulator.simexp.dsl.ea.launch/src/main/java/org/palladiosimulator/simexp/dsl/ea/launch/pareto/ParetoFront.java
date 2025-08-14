package org.palladiosimulator.simexp.dsl.ea.launch.pareto;

import java.util.List;
import java.util.Map;

public class ParetoFront {
    public static class ParetoEntry {
        public final double fitness;
        public final Map<String, Object> optimizables;

        public ParetoEntry(double fitness, Map<String, Object> optimizables) {
            this.fitness = fitness;
            this.optimizables = optimizables;
        }
    }

    public final List<ParetoEntry> paretoFront;

    public ParetoFront(List<ParetoEntry> entries) {
        this.paretoFront = entries;
    }

}
