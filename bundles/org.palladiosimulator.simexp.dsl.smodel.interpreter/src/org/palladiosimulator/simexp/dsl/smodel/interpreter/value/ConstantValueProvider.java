package org.palladiosimulator.simexp.dsl.smodel.interpreter.value;

import org.palladiosimulator.simexp.dsl.smodel.interpreter.ExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Constant;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Field;

public class ConstantValueProvider implements IFieldValueProvider {
    private final ExpressionCalculator expressionCalculator;

    public ConstantValueProvider() {
        this.expressionCalculator = new ExpressionCalculator(this);
    }

    @Override
    public Boolean getBoolValue(Field field) {
        Constant constant = (Constant) field;
        Expression value = constant.getValue();
        boolean calculatedBoolean = expressionCalculator.calculateBoolean(value);
        return calculatedBoolean;
    }

    @Override
    public Double getDoubleValue(Field field) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getIntegerValue(Field field) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getStringValue(Field field) {
        // TODO Auto-generated method stub
        return null;
    }

}
