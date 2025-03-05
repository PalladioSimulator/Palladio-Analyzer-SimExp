package org.palladiosimulator.simexp.dsl.ea.api.dispatcher;

import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;

public interface IDisposeableEAFitnessEvaluator extends IEAFitnessEvaluator {
    void init();

    void dispose();
}
