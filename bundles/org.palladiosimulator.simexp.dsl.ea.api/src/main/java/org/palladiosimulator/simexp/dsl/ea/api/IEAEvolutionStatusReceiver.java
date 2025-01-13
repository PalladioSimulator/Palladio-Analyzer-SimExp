package org.palladiosimulator.simexp.dsl.ea.api;

import java.util.List;

public interface IEAEvolutionStatusReceiver {
    /**
     * Called to report the current status of the EA.
     * 
     * @param optimizableValues
     *            the optimizables with the currently highest fitness
     * @param fitness
     *            the fitness of the given optimization values
     */
    void reportStatus(List<OptimizableValue<?>> optimizableValues, double fitness);
}
