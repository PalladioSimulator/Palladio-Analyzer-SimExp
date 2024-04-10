package org.palladiosimulator.simexp.dsl.smodel.interpreter.value;

import java.util.HashMap;
import java.util.Map;

import org.palladiosimulator.simexp.dsl.smodel.interpreter.ExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Field;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Variable;

public class VariableValueProvider implements IFieldValueProvider {

    private final ExpressionCalculator expressionCalculator;
    private final Map<Variable, Object> variableValueMap;

    public VariableValueProvider(ExpressionCalculator expressionCalculator) {
        this.expressionCalculator = expressionCalculator;
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
        return null;
    }

    @Override
    public Integer getIntegerValue(Field field) {
        return null;
    }

    @Override
    public String getStringValue(Field field) {
        return null;
    }
}
