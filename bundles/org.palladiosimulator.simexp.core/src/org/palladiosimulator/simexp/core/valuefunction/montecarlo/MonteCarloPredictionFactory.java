package org.palladiosimulator.simexp.core.valuefunction.montecarlo;

import org.palladiosimulator.simexp.core.valuefunction.ValueFunction;

public class MonteCarloPredictionFactory {

    public MonteCarloPrediction createFirstVisitEstimator() {
        IAccumulatedRewardManager accRewardManager = new AccumulatedRewardManager();
        ValueFunction valueFunction = new ValueFunction();
        MonteCaroEstimator estimator = new FirstVisitMonteCarloEstimator(accRewardManager, valueFunction);
        MonteCarloPrediction prediction = new MonteCarloPrediction(valueFunction, estimator);
        return prediction;
    }
}
