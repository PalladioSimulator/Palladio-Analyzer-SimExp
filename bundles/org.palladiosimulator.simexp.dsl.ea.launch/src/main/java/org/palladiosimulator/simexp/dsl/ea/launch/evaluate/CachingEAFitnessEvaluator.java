package org.palladiosimulator.simexp.dsl.ea.launch.evaluate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.IQualityAttributeProvider;
import org.palladiosimulator.simexp.dsl.ea.api.dispatcher.IDisposeableEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.api.util.OptimizableValueToString;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class CachingEAFitnessEvaluator implements IDisposeableEAFitnessEvaluator {
    private static final Logger LOGGER = Logger.getLogger(CachingEAFitnessEvaluator.class);

    private final IDisposeableEAFitnessEvaluator delegate;
    private final Map<List<OptimizableValue<?>>, Future<Optional<Double>>> cache = new HashMap<>();
    private final ClassLoader classloader;

    public CachingEAFitnessEvaluator(IDisposeableEAFitnessEvaluator delegate) {
        this.delegate = delegate;
        this.classloader = Thread.currentThread()
            .getContextClassLoader();
    }

    @Override
    public synchronized Future<Optional<Double>> calcFitness(List<OptimizableValue<?>> optimizableValues)
            throws IOException {
        ClassLoader oldContextClassLoader = Thread.currentThread()
            .getContextClassLoader();
        Thread.currentThread()
            .setContextClassLoader(classloader);
        try {
            Future<Optional<Double>> future = cache.get(optimizableValues);
            OptimizableValueToString optimizableValueToString = new OptimizableValueToString();
            if (future == null) {
                LOGGER.info(String.format("cache miss: %s", optimizableValueToString.asString(optimizableValues)));
                future = delegate.calcFitness(optimizableValues);
                cache.put(optimizableValues, future);
            } else {
                LOGGER.info(String.format("cache hit: %s", optimizableValueToString.asString(optimizableValues)));
            }
            return future;
        } finally {
            Thread.currentThread()
                .setContextClassLoader(oldContextClassLoader);
        }
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

    @Override
    public int getParallelism() {
        return delegate.getParallelism();
    }

    @Override
    public IQualityAttributeProvider getQualityAttributeProvider() {
        return delegate.getQualityAttributeProvider();
    }
}
