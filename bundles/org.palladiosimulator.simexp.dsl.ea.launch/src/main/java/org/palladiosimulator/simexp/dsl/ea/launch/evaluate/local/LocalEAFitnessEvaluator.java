package org.palladiosimulator.simexp.dsl.ea.launch.evaluate.local;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.launch.evaluate.IDisposeableEAFitnessEvaluator;

public class LocalEAFitnessEvaluator implements IDisposeableEAFitnessEvaluator {
    protected static final Logger LOGGER = Logger.getLogger(LocalEAFitnessEvaluator.class);

    private final ExecutorService executor;

    public LocalEAFitnessEvaluator() {
        this.executor = Executors.newFixedThreadPool(1);
    }

    @Override
    public void dispose() {
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public Future<Double> calcFitness(List<OptimizableValue<?>> optimizableValues) {
        Future<Double> future = executor.submit(new Callable<Double>() {

            @Override
            public Double call() throws Exception {
                return doCalcFitness(optimizableValues);
            }
        });
        return future;
    }

    private Double doCalcFitness(List<OptimizableValue<?>> optimizableValues) {
        // TODO:
        return null;
    }
}
