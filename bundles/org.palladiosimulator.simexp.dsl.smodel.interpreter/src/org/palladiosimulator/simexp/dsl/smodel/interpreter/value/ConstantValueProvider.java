package org.palladiosimulator.simexp.dsl.smodel.interpreter.value;

import org.palladiosimulator.simexp.dsl.smodel.interpreter.ExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Constant;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Field;
import org.palladiosimulator.simexp.dsl.smodel.smodel.StringLiteral;

public class ConstantValueProvider implements IFieldValueProvider {
    private final ExpressionCalculator expressionCalculator;

    public ConstantValueProvider() {
        this.expressionCalculator = new ExpressionCalculator(this);
    }

    @Override
    public Boolean getBoolValue(Field field) {
        Constant constant = (Constant) field;
        Expression value = constant.getValue();
        boolean calculatedValue = expressionCalculator.calculateBoolean(value);
        return calculatedValue;
    }

    @Override
    public Double getDoubleValue(Field field) {
        Constant constant = (Constant) field;
        Expression value = constant.getValue();
        double calculatedValue = expressionCalculator.calculateDouble(value);
        return calculatedValue;
    }

    @Override
    public Integer getIntegerValue(Field field) {
        Constant constant = (Constant) field;
        Expression value = constant.getValue();
        int calculatedValue = expressionCalculator.calculateInteger(value);
        return calculatedValue;
    }

    @Override
    public String getStringValue(Field field) {
        Constant constant = (Constant) field;
        Expression value = constant.getValue();
        StringLiteral stringLiteral = (StringLiteral) value.getLiteral();
        return stringLiteral.getValue();
    }

}
