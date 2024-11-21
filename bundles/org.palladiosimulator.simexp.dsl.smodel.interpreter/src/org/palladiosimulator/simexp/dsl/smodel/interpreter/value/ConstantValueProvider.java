package org.palladiosimulator.simexp.dsl.smodel.interpreter.value;

import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.ISmodelConfig;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.impl.ExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Constant;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Field;

public class ConstantValueProvider implements IFieldValueProvider {
    private final IExpressionCalculator expressionCalculator;

    public ConstantValueProvider(ISmodelConfig smodelConfig) {
        this.expressionCalculator = new ExpressionCalculator(smodelConfig, this);
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
        String calculatedValue = expressionCalculator.calculateString(value);
        return calculatedValue;
    }
}
