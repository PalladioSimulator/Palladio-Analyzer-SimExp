package org.palladiosimulator.simexp.dsl.smodel.interpreter.value;

import java.util.HashMap;
import java.util.Map;

import org.palladiosimulator.simexp.dsl.smodel.interpreter.ExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Field;
import org.palladiosimulator.simexp.dsl.smodel.smodel.StringLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Variable;

public class VariableValueProvider implements IFieldValueProvider {

    private final ExpressionCalculator expressionCalculator;
    private final Map<Variable, Object> variableValueMap;

    public VariableValueProvider(IFieldValueProvider fieldValueProvider) {
        this.expressionCalculator = new ExpressionCalculator(fieldValueProvider);
        this.variableValueMap = new HashMap<>();
    }

    @Override
    public Boolean getBoolValue(Field field) {
        Variable variable = (Variable) field;
        Object currentValue = variableValueMap.get(variable);
        if (currentValue != null) {
            return (Boolean) currentValue;
        }
        Expression value = variable.getValue();
        Boolean calculatedValue = expressionCalculator.calculateBoolean(value);
        variableValueMap.put(variable, calculatedValue);
        return calculatedValue;
    }

    @Override
    public Double getDoubleValue(Field field) {
        Variable variable = (Variable) field;
        Object currentValue = variableValueMap.get(variable);
        if (currentValue != null) {
            return (Double) currentValue;
        }
        Expression value = variable.getValue();
        Double calculatedValue = expressionCalculator.calculateDouble(value);
        variableValueMap.put(variable, calculatedValue);
        return calculatedValue;
    }

    @Override
    public Integer getIntegerValue(Field field) {
        Variable variable = (Variable) field;
        Object currentValue = variableValueMap.get(variable);
        if (currentValue != null) {
            return (Integer) currentValue;
        }
        Expression value = variable.getValue();
        Integer calculatedValue = expressionCalculator.calculateInteger(value);
        variableValueMap.put(variable, calculatedValue);
        return calculatedValue;
    }

    @Override
    public String getStringValue(Field field) {
        Variable variable = (Variable) field;
        Object currentValue = variableValueMap.get(variable);
        if (currentValue != null) {
            return (String) currentValue;
        }
        Expression value = variable.getValue();
        StringLiteral literal = (StringLiteral) value.getLiteral();
        String calculatedValue = literal.getValue();
        variableValueMap.put(variable, calculatedValue);
        return calculatedValue;
    }
}
