package org.palladiosimulator.simexp.dsl.ea.launch.evaluate.local;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;

public class LocalEAFitnessEvaluator implements IEAFitnessEvaluator {
    private final ExecutorService executor;

    public LocalEAFitnessEvaluator(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public Future<Double> calcFitness(List<OptimizableValue<?>> optimizableValues) {
        // TODO Auto-generated method stub
        return null;
    }

}
