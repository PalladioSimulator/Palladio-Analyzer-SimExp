package org.palladiosimulator.simexp.dsl.ea.api;

import java.util.List;
import java.util.concurrent.Future;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

/**
 * Interface to calculate the fitness of of {@link Optimizable} values.
 */
public interface IEAFitnessEvaluator {
    static class OptimizableValue<V> {
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

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37).append(optimizable.getName())
                .append(value)
                .toHashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }
            @SuppressWarnings("unchecked")
            OptimizableValue<V> rhs = (OptimizableValue<V>) obj;
            return new EqualsBuilder().appendSuper(super.equals(obj))
                .append(optimizable.getName(), rhs.getOptimizable()
                    .getName())
                .append(value, rhs.getValue())
                .isEquals();
        }
    }

    /**
     * Evaluates the fitness of a set of {@link Optimizable} values.
     * 
     * @param optimizableValues
     *            the optimizables to evaluate
     * @return a future receiving the fitness of the provided {@link Optimizable} values
     */
    Future<Double> calcFitness(List<OptimizableValue<?>> optimizableValues);

}
