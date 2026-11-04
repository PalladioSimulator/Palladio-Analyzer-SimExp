package org.palladiosimulator.simexp.dsl.bf.launch.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.api.IPrecisionProvider;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Bounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.StartKind;
import org.palladiosimulator.simexp.dsl.smodel.smodel.StopKind;

public class OptimizableValuesIterator implements Iterator<List<OptimizableValue<?>>> {
    private final IExpressionCalculator expressionCalculator;

    private final Iterator<List<OptimizableValue<?>>> iterator;

    public OptimizableValuesIterator(Collection<Optimizable> optimizables, IExpressionCalculator expressionCalculator) {
        this.expressionCalculator = expressionCalculator;
        this.iterator = combinations(optimizables);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public List<OptimizableValue<?>> next() {
        return iterator.next();
    }

    private Iterator<List<OptimizableValue<?>>> combinations(Collection<Optimizable> optimizables) {
        List<Optimizable> dimensions = new ArrayList<>(optimizables);

        return new Iterator<>() {
            private final List<Iterator<OptimizableValue<?>>> iterators = new ArrayList<>(
                    Collections.nCopies(dimensions.size(), null));

            private final List<OptimizableValue<?>> current = new ArrayList<>(
                    Collections.nCopies(dimensions.size(), null));

            private boolean started;
            private boolean prepared;
            private boolean exhausted;

            @Override
            public boolean hasNext() {
                if (prepared) {
                    return true;
                }
                if (exhausted) {
                    return false;
                }

                int resetFrom = 0;

                if (started) {
                    // Find the rightmost dimension that can advance.
                    int i = dimensions.size() - 1;
                    while (i >= 0 && !iterators.get(i)
                        .hasNext()) {
                        i--;
                    }

                    if (i < 0) {
                        exhausted = true;
                        return false;
                    }

                    current.set(i, iterators.get(i)
                        .next());
                    resetFrom = i + 1;
                }

                started = true;

                // Initialize or restart all following dimensions.
                for (int i = resetFrom; i < dimensions.size(); i++) {
                    Iterator<OptimizableValue<?>> values = valuesOf(dimensions.get(i));

                    if (!values.hasNext()) {
                        exhausted = true;
                        return false;
                    }

                    iterators.set(i, values);
                    current.set(i, values.next());
                }

                prepared = true;
                return true;
            }

            @Override
            public List<OptimizableValue<?>> next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                prepared = false;
                // Each result has its own list.
                return new ArrayList<>(current);
            }
        };
    }

    private Iterator<OptimizableValue<?>> valuesOf(Optimizable optimizable) {
        Bounds bounds = optimizable.getValues();
        if (bounds instanceof SetBounds setBounds) {
            return valuesOfSet(optimizable, setBounds);
        }
        if (bounds instanceof RangeBounds rangeBounds) {
            return valuesOfRange(optimizable, rangeBounds);
        }

        throw new RuntimeException("invalid bounds: " + bounds);
    }

    private Iterator<OptimizableValue<?>> valuesOfSet(Optimizable optimizable, SetBounds setBounds) {
        EList<Expression> setValues = setBounds.getValues();
        return setValues.stream()
            .<OptimizableValue<?>> map(value -> toOptimizableValue(optimizable, value))
            .iterator();
    }

    private OptimizableValue<?> toOptimizableValue(Optimizable optimizable, Expression value) {
        DataType dataType = optimizable.getDataType();
        switch (dataType) {
        case BOOL:
            return new OptimizableValue<>(optimizable, expressionCalculator.calculateBoolean(value));
        case STRING:
            return new OptimizableValue<>(optimizable, expressionCalculator.calculateString(value));
        case INT:
            return new OptimizableValue<>(optimizable, expressionCalculator.calculateInteger(value));
        case DOUBLE:
            return new OptimizableValue<>(optimizable, expressionCalculator.calculateDouble(value));
        default:
            throw new RuntimeException("Unsupported type: " + dataType);
        }
    }

    private Iterator<OptimizableValue<?>> valuesOfRange(Optimizable optimizable, RangeBounds rangeBounds) {
        DataType dataType = optimizable.getDataType();
        switch (dataType) {
        case INT:
            return getValueListInt(optimizable, rangeBounds);
        case DOUBLE:
            return getValueListDouble(optimizable, rangeBounds);
        default:
            throw new RuntimeException("Unsupported type: " + dataType);
        }
    }

    private Iterator<OptimizableValue<?>> getValueListInt(Optimizable optimizable, RangeBounds rangeBounds) {
        int startValue = expressionCalculator.calculateInteger(rangeBounds.getStartValue());
        int endValue = expressionCalculator.calculateInteger(rangeBounds.getEndValue());
        int stepSize = expressionCalculator.calculateInteger(rangeBounds.getStepSize());

        if (rangeBounds.getStart() == StartKind.OPEN) {
            startValue += stepSize;
        }

        IntStream stream = IntStream.iterate(startValue, n -> n + stepSize)
            .takeWhile(n -> n <= endValue);
        if (rangeBounds.getStop() == StopKind.OPEN) {
            stream = stream.filter(n -> n != endValue);
        }
        return stream.boxed()
            .<OptimizableValue<?>> map(value -> new OptimizableValue<>(optimizable, value))
            .iterator();
    }

    private Iterator<OptimizableValue<?>> getValueListDouble(Optimizable optimizable, RangeBounds rangeBounds) {
        double startValue = expressionCalculator.calculateDouble(rangeBounds.getStartValue());
        double endValue = expressionCalculator.calculateDouble(rangeBounds.getEndValue());
        double stepSize = expressionCalculator.calculateDouble(rangeBounds.getStepSize());

        if (rangeBounds.getStart() == StartKind.OPEN) {
            startValue += stepSize;
        }

        DoubleStream stream = DoubleStream.iterate(startValue, n -> n + stepSize)
            .takeWhile(n -> n <= endValue);
        if (rangeBounds.getStop() == StopKind.OPEN) {
            IPrecisionProvider precisionProvider = expressionCalculator.getPrecisionProvider();
            double epsilon = precisionProvider.getPrecision();
            stream = stream.filter(n -> Math.abs(n - endValue) >= epsilon);
        }
        return stream.boxed()
            .<OptimizableValue<?>> map(value -> new OptimizableValue<>(optimizable, value))
            .iterator();
    }
}
