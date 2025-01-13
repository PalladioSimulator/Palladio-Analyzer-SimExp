package org.palladiosimulator.simexp.dsl.ea.api;

import java.util.List;
import java.util.concurrent.Future;

import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.model.strategy.OptimizableValue;

/**
 * Interface to calculate the fitness of of {@link Optimizable} values.
 */
public interface IEAFitnessEvaluator {
    /**
     * Evaluates the fitness of a set of {@link Optimizable} values.
     * 
     * @param optimizableValues
     *            the optimizables to evaluate
     * @return a future receiving the fitness of the provided {@link Optimizable} values
     */
    Future<Double> calcFitness(List<OptimizableValue<?>> optimizableValues);

}
