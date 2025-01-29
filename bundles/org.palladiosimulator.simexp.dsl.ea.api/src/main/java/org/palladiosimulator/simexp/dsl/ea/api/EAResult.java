package org.palladiosimulator.simexp.dsl.ea.api;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class EAResult {
    private final double[] fitness;
    private final List<OptimizableValue<?>> optimizableValues;

    public EAResult(double[] fitness, List<OptimizableValue<?>> optimizableValues) {
        this.optimizableValues = new ArrayList<>(optimizableValues);
        this.fitness = fitness;
    }

    public double[] getFitness() {
        return fitness;
    }

    public List<OptimizableValue<?>> getOptimizableValues() {
        return optimizableValues;
    }

}
