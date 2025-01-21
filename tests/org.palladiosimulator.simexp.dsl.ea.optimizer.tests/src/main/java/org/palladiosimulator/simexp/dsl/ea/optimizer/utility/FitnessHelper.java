package org.palladiosimulator.simexp.dsl.ea.optimizer.utility;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.mockito.invocation.InvocationOnMock;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;

import io.jenetics.internal.collection.ArrayISeq;
import io.jenetics.internal.collection.Empty.EmptyISeq;

public class FitnessHelper {
    @SuppressWarnings("rawtypes")
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

    @SuppressWarnings("rawtypes")
    private Double getNextFitness(List<IEAFitnessEvaluator.OptimizableValue> optimizableValues) {
        double value = 0;

        for (IEAFitnessEvaluator.OptimizableValue singleOptimizableValue : optimizableValues) {
            Object apply = singleOptimizableValue.getValue();
            DataType optimizableDataType = singleOptimizableValue.getOptimizable()
                .getDataType();

            //TODO nbruening: Remove Seq support?
            if (apply instanceof ArrayISeq arraySeq) {
                if (arraySeq.size() == 1) {
                    for (Object element : arraySeq.array) {
                        if (optimizableDataType == DataType.INT) {
                            value += (Integer) element;
                        } else if (optimizableDataType == DataType.DOUBLE) {
                            value += (Double) element;
                        } else if (optimizableDataType == DataType.STRING) {
                            value += ((String) element).length();
                        }

                    }
                }
            } else if (apply instanceof EmptyISeq emptySeq) {
                // do nothing
            } else if (optimizableDataType == DataType.DOUBLE) {
                if (apply != null)
                    value += (Double) apply;
            } else if (optimizableDataType == DataType.INT) {
                if (apply != null)
                    value += (Integer) apply;
            } else if (optimizableDataType == DataType.BOOL) {
                if ((apply != null) && ((Boolean) apply)) {
                    value += 50;
                }
            } else if (optimizableDataType == DataType.STRING) {
                if (apply != null) {
                    value += ((String) apply).length();
                }
            }

            else {
                throw new RuntimeException("Received unexpected data type: " + optimizableDataType);
            }
        }

        return value;
    }

}
