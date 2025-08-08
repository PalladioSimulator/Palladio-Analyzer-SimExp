package org.palladiosimulator.simexp.dsl.ea.api;

import java.util.Collections;
import java.util.List;

import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class EAResult {
    public static class IndividualResult {
        private final double fitness;
        private final List<OptimizableValue<?>> optimizableValues;

        public IndividualResult(double fitness, List<OptimizableValue<?>> optimizableValues) {
            this.fitness = fitness;
            this.optimizableValues = Collections.unmodifiableList(optimizableValues);
        }

        public double getFitness() {
            return fitness;
        }

        public List<OptimizableValue<?>> getOptimizableValues() {
            return optimizableValues;
        }
    }

    private final IndividualResult fittestIndividual;
    private final List<List<OptimizableValue<?>>> equivalentOptimizableValuesList;
    private final List<IndividualResult> finalPopulation;

    public EAResult(IndividualResult fittestIndividual, List<List<OptimizableValue<?>>> equivalentOptimizableValuesList,
            List<IndividualResult> finalPopulation) {
        this.fittestIndividual = fittestIndividual;
        this.equivalentOptimizableValuesList = Collections.unmodifiableList(equivalentOptimizableValuesList);
        this.finalPopulation = Collections.unmodifiableList(finalPopulation);
    }

    public IndividualResult getFittest() {
        return fittestIndividual;
    }

    public List<List<OptimizableValue<?>>> getEquivalentOptimizableValues() {
        return equivalentOptimizableValuesList;
    }

    public List<IndividualResult> getFinalPopulation() {
        return finalPopulation;
    }
}
