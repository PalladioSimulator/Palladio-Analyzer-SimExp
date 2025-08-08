package org.palladiosimulator.simexp.dsl.ea.api;

import java.util.Collections;
import java.util.List;

import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class EAResult {

    private final double fitness;
    private final List<OptimizableValue<?>> bestOptimizableValues;
    private final List<List<OptimizableValue<?>>> equivalentOptimizableValuesList;

    public EAResult(double fitness, List<OptimizableValue<?>> bestOptimizableValues,
            List<List<OptimizableValue<?>>> equivalentOptimizableValuesList) {
        this.fitness = fitness;
        this.bestOptimizableValues = Collections.unmodifiableList(bestOptimizableValues);
        this.equivalentOptimizableValuesList = Collections.unmodifiableList(equivalentOptimizableValuesList);
    }

    public double getFitness() {
        return fitness;
    }

    public List<OptimizableValue<?>> getBestOptimizableValues() {
        return bestOptimizableValues;
    }

    public List<List<OptimizableValue<?>>> getEquivalentOptimizableValues() {
        return equivalentOptimizableValuesList;
    }
}
