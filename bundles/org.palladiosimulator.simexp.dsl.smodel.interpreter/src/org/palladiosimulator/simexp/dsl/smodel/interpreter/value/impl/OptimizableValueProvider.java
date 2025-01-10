package org.palladiosimulator.simexp.dsl.smodel.interpreter.value.impl;

import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Bounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DoubleLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Field;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;

/**
 * Samples the Optimizable values.
 * 
 * @author rapp
 *
 */
public class OptimizableValueProvider implements IFieldValueProvider {

    public OptimizableValueProvider() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public Boolean getBoolValue(Field field) {
        throw new UnsupportedOperationException("Boolean Field not supported");
    }

    @Override
    public Double getDoubleValue(Field field) {
        if (field instanceof Optimizable) {
            Optimizable optimizable = (Optimizable) field;
            if (optimizable.getDataType() == DataType.DOUBLE) {
                Bounds bounds = optimizable.getValues();
                if (bounds instanceof SetBounds) {
                    SetBounds setBounds = (SetBounds) bounds;
                    Expression expression = setBounds.getValues()
                        .get(0);
                    DoubleLiteral doubleLiteral = (DoubleLiteral) expression;
                    return doubleLiteral.getValue();
                }
                if (bounds instanceof RangeBounds) {
                    RangeBounds rangeBounds = (RangeBounds) bounds;
                    Expression expression = rangeBounds.getStartValue();
                    DoubleLiteral doubleLiteral = (DoubleLiteral) expression;
                    return doubleLiteral.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public Integer getIntegerValue(Field field) {
        if (field instanceof Optimizable) {
            Optimizable optimizable = (Optimizable) field;
            if (optimizable.getDataType() == DataType.INT) {
                Bounds bounds = optimizable.getValues();
                if (bounds instanceof SetBounds) {
                    SetBounds setBounds = (SetBounds) bounds;
                    Expression expression = setBounds.getValues()
                        .get(0);
                    IntLiteral intLiteral = (IntLiteral) expression;
                    return intLiteral.getValue();
                }
                if (bounds instanceof RangeBounds) {
                    RangeBounds rangeBounds = (RangeBounds) bounds;
                    Expression expression = rangeBounds.getStartValue();
                    IntLiteral intLiteral = (IntLiteral) expression;
                    return intLiteral.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public String getStringValue(Field field) {
        throw new UnsupportedOperationException("String Field not supported");
    }

}
