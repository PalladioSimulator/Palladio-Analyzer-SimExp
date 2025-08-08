package org.palladiosimulator.simexp.dsl.ea.api;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

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
    Future<Optional<Double>> calcFitness(List<OptimizableValue<?>> optimizableValues) throws IOException;

    /**
     * Returns the parallelism provided by the evaluator.
     * 
     * @return the parallelism of the evaluator
     */
    int getParallelism();

    IQualityAttributeProvider getQualityAttributeProvider();
}
