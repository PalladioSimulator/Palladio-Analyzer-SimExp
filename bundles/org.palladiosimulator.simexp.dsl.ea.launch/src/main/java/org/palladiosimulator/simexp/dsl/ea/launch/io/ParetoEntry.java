package org.palladiosimulator.simexp.dsl.ea.launch.io;

import java.util.Map;

class ParetoEntry extends ResultEntry {
    public final double score;
    public final Map<String, Double> averages;

    public ParetoEntry(double fitness, Map<String, Object> optimizables, String id, double score,
            Map<String, Double> averages) {
        super(fitness, optimizables, id);
        this.score = score;
        this.averages = averages;
    }
}
