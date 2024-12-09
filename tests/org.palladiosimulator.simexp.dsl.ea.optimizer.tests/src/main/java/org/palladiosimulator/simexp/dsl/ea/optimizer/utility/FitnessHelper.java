package org.palladiosimulator.simexp.dsl.ea.optimizer.utility;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.mockito.invocation.InvocationOnMock;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.DecoderEncodingPair;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;

import io.jenetics.internal.collection.ArrayISeq;
import io.jenetics.internal.collection.Empty.EmptyISeq;

public class FitnessHelper {
    public Future<Double> getFitnessFunctionAsFuture(InvocationOnMock invocation) {
        List<IEAFitnessEvaluator.OptimizableValue> optimizableValues = invocation.getArgument(0);
        Double fitnessValue = getNextFitness(optimizableValues);
        return new Future<>() {

            @Override
            public boolean isDone() {
                return true;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public Double get(long timeout, TimeUnit unit)
                    throws InterruptedException, ExecutionException, TimeoutException {
                return fitnessValue;
            }

            @Override
            public Double get() throws InterruptedException, ExecutionException {
                return fitnessValue;
            }

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }
        };
    }

    private Double getNextFitness(List<IEAFitnessEvaluator.OptimizableValue> optimizableValues) {
        double value = 0;

        for (IEAFitnessEvaluator.OptimizableValue singleOptimizableValue : optimizableValues) {
            DecoderEncodingPair chromoPair = (DecoderEncodingPair) singleOptimizableValue.getValue();
            DataType optimizableDataType = singleOptimizableValue.getOptimizable()
                .getDataType();

            Object apply = chromoPair.first()
                .apply(chromoPair.second());

            if (apply instanceof ArrayISeq arraySeq) {
                if (arraySeq.size() == 1) {
                    for (Object element : arraySeq.array) {
                        if (optimizableDataType == DataType.INT) {
                            value += (Integer) element;
                        }
                    }
                }
            } else if (apply instanceof EmptyISeq emptySeq) {
                // do nothing
            } else {
                return null;
            }
        }

        return value;
    }

}
