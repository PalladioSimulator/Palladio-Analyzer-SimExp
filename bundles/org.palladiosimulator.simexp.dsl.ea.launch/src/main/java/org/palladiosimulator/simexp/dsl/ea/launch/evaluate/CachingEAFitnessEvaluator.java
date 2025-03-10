package org.palladiosimulator.simexp.dsl.ea.launch.evaluate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.dispatcher.IDisposeableEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class CachingEAFitnessEvaluator implements IDisposeableEAFitnessEvaluator {
    private static final Logger LOGGER = Logger.getLogger(CachingEAFitnessEvaluator.class);

    private final IDisposeableEAFitnessEvaluator delegate;
    private final Map<List<OptimizableValue<?>>, Future<Optional<Double>>> cache = new HashMap<>();

    public CachingEAFitnessEvaluator(IDisposeableEAFitnessEvaluator delegate) {
        this.delegate = delegate;
    }

    @Override
    public synchronized Future<Optional<Double>> calcFitness(List<OptimizableValue<?>> optimizableValues) {
        Future<Optional<Double>> future = cache.get(optimizableValues);
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

    private class EvaluatorClientDelegate implements EvaluatorClient {
        private final EvaluatorClient delegate;

        public EvaluatorClientDelegate(EvaluatorClient delegate) {
            this.delegate = delegate;
        }

        @Override
        public void process(IEAFitnessEvaluator evaluator) {
            delegate.process(CachingEAFitnessEvaluator.this);
        }
    }

    @Override
    public void evaluate(EvaluatorClient client) {
        try {
            EvaluatorClientDelegate clientDelegate = new EvaluatorClientDelegate(client);
            delegate.evaluate(clientDelegate);
        } finally {
            cache.clear();
        }
    }
}
