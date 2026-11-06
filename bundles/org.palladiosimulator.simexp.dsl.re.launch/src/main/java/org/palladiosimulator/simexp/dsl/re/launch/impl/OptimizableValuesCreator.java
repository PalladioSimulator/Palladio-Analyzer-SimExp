package org.palladiosimulator.simexp.dsl.re.launch.impl;

import java.util.Collection;
import java.util.List;
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

public class OptimizableValuesCreator {
    private final IExpressionCalculator expressionCalculator;

    public OptimizableValuesCreator(IExpressionCalculator expressionCalculator) {
        this.expressionCalculator = expressionCalculator;
    }

    public List<List<OptimizableValue<?>>> combinations(Collection<Optimizable> optimizables) {
        return optimizables.stream()
            .map(o -> valuesOf(o))
            .toList();
    }

    private List<OptimizableValue<?>> valuesOf(Optimizable optimizable) {
        Bounds bounds = optimizable.getValues();
        if (bounds instanceof SetBounds setBounds) {
            return valuesOfSet(optimizable, setBounds);
        }
        if (bounds instanceof RangeBounds rangeBounds) {
            return valuesOfRange(optimizable, rangeBounds);
        }

        throw new RuntimeException("invalid bounds: " + bounds);
    }

    private List<OptimizableValue<?>> valuesOfSet(Optimizable optimizable, SetBounds setBounds) {
        EList<Expression> setValues = setBounds.getValues();
        return setValues.stream()
            .<OptimizableValue<?>> map(value -> toOptimizableValue(optimizable, value))
            .toList();
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

    private List<OptimizableValue<?>> valuesOfRange(Optimizable optimizable, RangeBounds rangeBounds) {
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

    private List<OptimizableValue<?>> getValueListInt(Optimizable optimizable, RangeBounds rangeBounds) {
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
            .toList();
    }

    private List<OptimizableValue<?>> getValueListDouble(Optimizable optimizable, RangeBounds rangeBounds) {
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
            .toList();
    }
}
