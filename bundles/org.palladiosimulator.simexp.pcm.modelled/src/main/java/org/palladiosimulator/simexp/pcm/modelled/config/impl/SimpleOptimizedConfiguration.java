package org.palladiosimulator.simexp.pcm.modelled.config.impl;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Bounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DoubleLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.StringLiteral;
import org.palladiosimulator.simexp.pcm.modelled.config.IOptimizedConfiguration;

public class SimpleOptimizedConfiguration implements IOptimizedConfiguration {
    private final List<Optimizable> optimizables;

    public SimpleOptimizedConfiguration(Smodel smodel) {
        this(smodel.getOptimizables());
    }

    SimpleOptimizedConfiguration(List<Optimizable> optimizables) {
        this.optimizables = optimizables;
    }

    @Override
    public List<OptimizableValue<?>> getOptimizableValues() {
        List<OptimizableValue<?>> values = new ArrayList<>();
        for (Optimizable optimizable : optimizables) {
            OptimizableValue<?> value = getOptimizableValue(optimizable);
            values.add(value);
        }
        return values;
    }

    OptimizableValue<?> getOptimizableValue(Optimizable optimizable) {
        DataType dataType = optimizable.getDataType();
        switch (dataType) {
        case BOOL:
            return getOptimizableValueBool(optimizable);
        case STRING:
            return getOptimizableValueString(optimizable);
        case INT:
            return getOptimizableValueInt(optimizable);
        case DOUBLE:
            return getOptimizableValueDouble(optimizable);
        default:
            throw new RuntimeException("Unsupported type: " + dataType);
        }
    }

    private OptimizableValue<Boolean> getOptimizableValueBool(Optimizable optimizable) {
        Bounds bounds = optimizable.getValues();
        if (bounds instanceof SetBounds) {
            SetBounds setBounds = (SetBounds) bounds;
            Expression expression = setBounds.getValues()
                .get(0);
            BoolLiteral literal = (BoolLiteral) expression;
            return new OptimizableValue<>(optimizable, literal.isTrue());
        }
        throw new RuntimeException("unsupported bounds: " + bounds);
    }

    private OptimizableValue<String> getOptimizableValueString(Optimizable optimizable) {
        Bounds bounds = optimizable.getValues();
        if (bounds instanceof SetBounds) {
            SetBounds setBounds = (SetBounds) bounds;
            Expression expression = setBounds.getValues()
                .get(0);
            StringLiteral literal = (StringLiteral) expression;
            return new OptimizableValue<>(optimizable, literal.getValue());
        }
        throw new RuntimeException("unsupported bounds: " + bounds);
    }

    private OptimizableValue<Integer> getOptimizableValueInt(Optimizable optimizable) {
        Bounds bounds = optimizable.getValues();
        if (bounds instanceof SetBounds) {
            SetBounds setBounds = (SetBounds) bounds;
            Expression expression = setBounds.getValues()
                .get(0);
            IntLiteral intLiteral = (IntLiteral) expression;
            return new OptimizableValue<>(optimizable, intLiteral.getValue());
        }
        if (bounds instanceof RangeBounds) {
            RangeBounds rangeBounds = (RangeBounds) bounds;
            Expression expression = rangeBounds.getStartValue();
            IntLiteral intLiteral = (IntLiteral) expression;
            return new OptimizableValue<>(optimizable, intLiteral.getValue());
        }
        throw new RuntimeException("unsupported bounds: " + bounds);
    }

    private OptimizableValue<Double> getOptimizableValueDouble(Optimizable optimizable) {
        Bounds bounds = optimizable.getValues();
        if (bounds instanceof SetBounds) {
            SetBounds setBounds = (SetBounds) bounds;
            Expression expression = setBounds.getValues()
                .get(0);
            DoubleLiteral doubleLiteral = (DoubleLiteral) expression;
            return new OptimizableValue<>(optimizable, doubleLiteral.getValue());
        }
        if (bounds instanceof RangeBounds) {
            RangeBounds rangeBounds = (RangeBounds) bounds;
            Expression expression = rangeBounds.getStartValue();
            DoubleLiteral doubleLiteral = (DoubleLiteral) expression;
            return new OptimizableValue<>(optimizable, doubleLiteral.getValue());
        }
        throw new RuntimeException("unsupported bounds: " + bounds);
    }
}
