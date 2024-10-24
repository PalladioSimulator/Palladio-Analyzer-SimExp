package org.palladiosimulator.simexp.dsl.smodel.interpreter.value;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.ISmodelConfig;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.IVariableAssigner;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.impl.ExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.impl.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Field;
import org.palladiosimulator.simexp.dsl.smodel.smodel.StringLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Variable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.VariableAssignment;
import org.palladiosimulator.simexp.dsl.smodel.util.SmodelDataTypeSwitch;

public class VariableValueProvider implements IFieldValueProvider, IVariableAssigner {

    private final IExpressionCalculator expressionCalculator;
    private final SmodelDataTypeSwitch dataTypeSwitch;
    private final Map<Variable, Object> variableValueMap;

    public VariableValueProvider(ISmodelConfig smodelConfig, IFieldValueProvider constantValueProvider,
            IFieldValueProvider probeValueProvider, IFieldValueProvider optimizableValueProvider,
            IFieldValueProvider envVariableValueProvider) {
        IFieldValueProvider fieldValueProvider = new FieldValueProvider(constantValueProvider, this, probeValueProvider,
                optimizableValueProvider, envVariableValueProvider);
        this.expressionCalculator = new ExpressionCalculator(smodelConfig, fieldValueProvider);
        this.dataTypeSwitch = new SmodelDataTypeSwitch();
        Comparator<Variable> comparator = Comparator.comparing(Variable::getName);
        this.variableValueMap = new TreeMap<>(comparator);
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
        String calculatedValue = expressionCalculator.calculateString(value);
        variableValueMap.put(variable, calculatedValue);
        return calculatedValue;
    }

    @Override
    public void assign(VariableAssignment variableAssignment) {
        Variable variableRef = variableAssignment.getVariableRef();
        Expression value = variableAssignment.getValue();
        DataType dataType = dataTypeSwitch.doSwitch(variableRef);
        final Object calculatedValue;
        switch (dataType) {
        case BOOL:
            calculatedValue = expressionCalculator.calculateBoolean(value);
            break;
        case DOUBLE:
            calculatedValue = expressionCalculator.calculateDouble(value);
            break;
        case INT:
            calculatedValue = expressionCalculator.calculateInteger(value);
            break;
        case STRING:
            StringLiteral literal = (StringLiteral) value.getLiteral();
            calculatedValue = literal.getValue();
            break;
        default:
            throw new RuntimeException("unsupported DataType: " + dataType);
        }
        variableValueMap.put(variableRef, calculatedValue);
    }
}
