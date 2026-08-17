package org.palladiosimulator.simexp.dsl.ea.api.dispatcher;

import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;

public interface IDisposeableEAFitnessEvaluator extends IEAFitnessEvaluator {
    interface EvaluatorClient {
        void process(IEAFitnessEvaluator evaluator);
    }

    void evaluate(EvaluatorClient evaluatorClient);
}
