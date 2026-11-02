package org.palladiosimulator.simexp.dsl.dispatcher;

public interface IDisposeableEAFitnessEvaluator extends IEAFitnessEvaluator {
    interface EvaluatorClient {
        void process(IEAFitnessEvaluator evaluator);
    }

    void evaluate(EvaluatorClient evaluatorClient);
}
