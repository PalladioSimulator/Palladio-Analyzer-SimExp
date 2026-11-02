package org.palladiosimulator.simexp.dsl.ea.launch.evaluate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.dsl.dispatcher.IResultIdentificator;
import org.palladiosimulator.simexp.dsl.dispatcher.IDisposeableRewardEvaluator;
import org.palladiosimulator.simexp.dsl.dispatcher.IRewardEvaluator;
import org.palladiosimulator.simexp.dsl.dispatcher.IQualityAttributeProvider;
import org.palladiosimulator.simexp.dsl.dispatcher.util.OptimizableValueToString;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;

public class CachingEAFitnessEvaluator implements IDisposeableRewardEvaluator {
    private static final Logger LOGGER = Logger.getLogger(CachingEAFitnessEvaluator.class);

    private final IDisposeableRewardEvaluator delegate;
    private final Map<List<OptimizableValue<?>>, Future<Optional<Double>>> cache = new HashMap<>();
    private final ClassLoader classloader;

    public CachingEAFitnessEvaluator(IDisposeableRewardEvaluator delegate) {
        this.delegate = delegate;
        this.classloader = Thread.currentThread()
            .getContextClassLoader();
    }

    @Override
    public synchronized Future<Optional<Double>> calcReward(List<OptimizableValue<?>> optimizableValues)
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
                future = delegate.calcReward(optimizableValues);
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
        public void process(IRewardEvaluator evaluator) {
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

    @Override
    public IResultIdentificator getFitnessResultIdentificator() {
        return delegate.getFitnessResultIdentificator();
    }
}
