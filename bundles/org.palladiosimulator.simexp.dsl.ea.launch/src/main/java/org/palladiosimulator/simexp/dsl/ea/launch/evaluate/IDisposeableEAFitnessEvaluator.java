package org.palladiosimulator.simexp.dsl.ea.launch.evaluate;

import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;

public interface IDisposeableEAFitnessEvaluator extends IEAFitnessEvaluator {
    void dispose();
}
