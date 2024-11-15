package org.palladiosimulator.simexp.dsl.ea.api;

import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;

/**
 * Interface for concrete evolutionary algorithm optimizations.
 */
public interface IEAOptimizer {
    /**
     * 
     * @param optimizableProvider
     *            provides the available {@link Optimizable}s
     * @param fitnessEvaluator
     *            calculates the fitness of an concrete set of optimizable values
     * @param evolutionStatusReceiver
     *            receives the current status of the EA
     */
    void optimize(IOptimizableProvider optimizableProvider, IEAFitnessEvaluator fitnessEvaluator,
            IEAEvolutionStatusReceiver evolutionStatusReceiver);
}
