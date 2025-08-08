package org.palladiosimulator.simexp.dsl.ea.api;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class EAResult {

    private final double fitness;
    private final List<List<OptimizableValue<?>>> equivalentOptimizableValuesList;

    public EAResult(double fitness, List<List<OptimizableValue<?>>> equivalentOptimizableValuesList) {
        this.equivalentOptimizableValuesList = new ArrayList<>(equivalentOptimizableValuesList);
        this.fitness = fitness;
    }

    public double getFitness() {
        return fitness;
    }

    public List<List<OptimizableValue<?>>> getEquivalentOptimizableValues() {
        return equivalentOptimizableValuesList;
    }

}
