package org.palladiosimulator.simexp.dsl.ea.api;

import java.util.List;

import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

/**
 * Interface to calculate the fitness of of {@link Optimizable} values.
 */
public interface IEAFitnessEvaluator {
    class OptimizableValue<V> {
        private final Optimizable optimizable;
        private final V value;

        public OptimizableValue(Optimizable optimizable, V value) {
            this.optimizable = optimizable;
            this.value = value;
        }

        public Optimizable getOptimizable() {
            return optimizable;
        }

        public V getValue() {
            return value;
        }
    }

    /**
     * Evaluates the fitness of a set of {@link Optimizable} values.
     * 
     * @param optimizableValues
     *            the optimizables to evaluate
     * @return the fitness of the provided {@link Optimizable} values
     */
    double calcFitness(List<OptimizableValue<?>> optimizableValues);

}
