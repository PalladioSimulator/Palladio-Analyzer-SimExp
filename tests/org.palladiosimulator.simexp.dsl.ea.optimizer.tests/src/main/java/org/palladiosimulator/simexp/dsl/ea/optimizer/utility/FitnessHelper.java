package org.palladiosimulator.simexp.dsl.ea.optimizer.utility;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.mockito.invocation.InvocationOnMock;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;

public class FitnessHelper {
    private static class FixedFuture implements Future<Optional<Double>> {
        private final double fitnessValue;

        public FixedFuture(double fitnessValue) {
            this.fitnessValue = fitnessValue;
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public Optional<Double> get(long timeout, TimeUnit unit)
                throws InterruptedException, ExecutionException, TimeoutException {
            return Optional.of(fitnessValue);
        }

        @Override
        public Optional<Double> get() throws InterruptedException, ExecutionException {
            return Optional.of(fitnessValue);
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }
    }

    public Future<Optional<Double>> getFitnessFunctionAsFuture(InvocationOnMock invocation) {
        List<OptimizableValue<?>> optimizableValues = invocation.getArgument(0);
        double currentFitness = getCurrentFitness(optimizableValues);
        return new FixedFuture(currentFitness);
    }

    double getCurrentFitness(List<OptimizableValue<?>> optimizableValues) {
        double currentFitness = 0;

        for (OptimizableValue<?> singleOptimizableValue : optimizableValues) {
            Object optimizeableValue = singleOptimizableValue.getValue();
            DataType optimizableDataType = singleOptimizableValue.getOptimizable()
                .getDataType();

            if (optimizableDataType == DataType.DOUBLE) {
                if (optimizeableValue != null)
                    currentFitness += (Double) optimizeableValue;
            } else if (optimizableDataType == DataType.INT) {
                if (optimizeableValue != null)
                    currentFitness += (Integer) optimizeableValue;
            } else if (optimizableDataType == DataType.BOOL) {
                if ((optimizeableValue != null) && ((Boolean) optimizeableValue)) {
                    currentFitness += 50;
                }
            } else if (optimizableDataType == DataType.STRING) {
                if (optimizeableValue != null) {
                    currentFitness += ((String) optimizeableValue).length();
                }
            }

            else {
                throw new RuntimeException("Received unexpected data type: " + optimizableDataType);
            }
        }

        return currentFitness;
    }

}
