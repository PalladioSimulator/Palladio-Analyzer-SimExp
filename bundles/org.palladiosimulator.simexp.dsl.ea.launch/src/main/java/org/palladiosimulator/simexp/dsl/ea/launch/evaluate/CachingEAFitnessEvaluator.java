package org.palladiosimulator.simexp.dsl.ea.launch.evaluate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class CachingEAFitnessEvaluator implements IDisposeableEAFitnessEvaluator {
    private static final Logger LOGGER = Logger.getLogger(CachingEAFitnessEvaluator.class);

    private final IDisposeableEAFitnessEvaluator delegate;
    private final Map<List<OptimizableValue<?>>, Future<Double>> cache = new HashMap<>();

    public CachingEAFitnessEvaluator(IDisposeableEAFitnessEvaluator delegate) {
        this.delegate = delegate;
    }

    @Override
    public synchronized Future<Double> calcFitness(List<OptimizableValue<?>> optimizableValues) {
        Future<Double> future = cache.get(optimizableValues);
        if (future == null) {
            LOGGER.info(String.format("cache miss: %s", asString(optimizableValues)));
            future = delegate.calcFitness(optimizableValues);
            cache.put(optimizableValues, future);
        } else {
            LOGGER.info(String.format("cache hit: %s", asString(optimizableValues)));
        }
        return future;
    }

    private String asString(List<OptimizableValue<?>> optimizableValues) {
        List<String> entries = new ArrayList<>();
        for (OptimizableValue<?> ov : optimizableValues) {
            entries.add(String.format("%s: %s", ov.getOptimizable()
                .getName(), ov.getValue()));
        }
        return StringUtils.join(entries, ",");
    }

    @Override
    public void dispose() {
        cache.clear();
        delegate.dispose();
    }
}
