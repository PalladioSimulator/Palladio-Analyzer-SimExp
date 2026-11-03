package org.palladiosimulator.simexp.dsl.bf.launch.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Bounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;

public class OptimizableValuesIterator implements Iterator<List<OptimizableValue<?>>> {
    private final Collection<Optimizable> optimizables;
    private final IExpressionCalculator expressionCalculator;

    private int counter = 0;

    public OptimizableValuesIterator(Collection<Optimizable> optimizables, IExpressionCalculator expressionCalculator) {
        this.optimizables = optimizables;
        this.expressionCalculator = expressionCalculator;
    }

    @Override
    public boolean hasNext() {
        return counter == 0;
    }

    @Override
    public List<OptimizableValue<?>> next() {
        counter++;
        List<OptimizableValue<?>> optimizableValues = new ArrayList<>();
        for (Optimizable optimizable : optimizables) {
            OptimizableValue<?> optimizableValue = toOptimizableValue(optimizable);
            optimizableValues.add(optimizableValue);
        }
        return optimizableValues;
    }

    private OptimizableValue<?> toOptimizableValue(Optimizable optimizable) {
        Expression value = getValue(optimizable);
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

    private Expression getValue(Optimizable optimizable) {
        Bounds bounds = optimizable.getValues();
        if (bounds instanceof SetBounds setBounds) {
            return setBounds.getValues()
                .get(0);
        }
        if (bounds instanceof RangeBounds rangeBounds) {
            return rangeBounds.getStartValue();
        }
        throw new RuntimeException("invalid bounds: " + bounds);
    }
}
