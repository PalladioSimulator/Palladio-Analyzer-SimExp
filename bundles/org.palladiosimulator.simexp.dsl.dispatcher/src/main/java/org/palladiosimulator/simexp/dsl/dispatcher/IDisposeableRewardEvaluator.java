package org.palladiosimulator.simexp.dsl.dispatcher;

public interface IDisposeableRewardEvaluator extends IRewardEvaluator {
    interface EvaluatorClient {
        void process(IRewardEvaluator evaluator);
    }

    void evaluate(EvaluatorClient evaluatorClient);
}
