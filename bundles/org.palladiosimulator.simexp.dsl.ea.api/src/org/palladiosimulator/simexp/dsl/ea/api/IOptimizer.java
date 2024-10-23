package org.palladiosimulator.simexp.dsl.ea.api;

import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

/**
 * Interface for concrete evolutionary algorithm optimizations.
 */
public interface IOptimizer {
    /**
     * 
     * @param optimizableProvider
     *            provides the available {@link Optimizable}s
     * @param fitnessEvaluator
     *            calculates the fitness of an concrete set of optimizable values
     */
    void optimize(IOptimizableProvider optimizableProvider, IFitnessEvaluator fitnessEvaluator);
}
