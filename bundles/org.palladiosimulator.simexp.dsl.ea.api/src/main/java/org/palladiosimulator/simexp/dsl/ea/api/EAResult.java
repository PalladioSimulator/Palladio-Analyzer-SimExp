package org.palladiosimulator.simexp.dsl.ea.api;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class EAResult {
    private final double fitness;
    private final List<List<OptimizableValue<?>>> optimizableValuesList;

    public EAResult(double fitness, List<List<OptimizableValue<?>>> optimizableValuesList) {
        this.optimizableValuesList = new ArrayList<>(optimizableValuesList);
        this.fitness = fitness;
    }

    public double getFitness() {
        return fitness;
    }

    public List<List<OptimizableValue<?>>> getOptimizableValuesList() {
        return optimizableValuesList;
    }

}
