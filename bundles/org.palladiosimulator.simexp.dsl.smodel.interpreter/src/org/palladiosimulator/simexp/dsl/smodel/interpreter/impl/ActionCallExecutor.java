package org.palladiosimulator.simexp.dsl.smodel.interpreter.impl;

import java.util.HashMap;
import java.util.Map;

import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.ResolvedAction;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Action;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ActionArguments;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ActionCall;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ParameterValue;
import org.palladiosimulator.simexp.dsl.smodel.util.SmodelDataTypeSwitch;

public class ActionCallExecutor implements IActionCallExecutor {
    private final SmodelDataTypeSwitch typeSwitch;
    private final IExpressionCalculator expressionCalculator;
    private final IFieldValueProvider fieldValueProvider;

    public ActionCallExecutor(IExpressionCalculator expressionCalculator, IFieldValueProvider fieldValueProvider) {
        this.typeSwitch = new SmodelDataTypeSwitch();
        this.expressionCalculator = expressionCalculator;
        this.fieldValueProvider = fieldValueProvider;
    }

    @Override
    public ResolvedAction execute(ActionCall actionCall) {
        Action action = actionCall.getActionRef();
        Map<String, Object> arguments = resolveArguments(actionCall);
        ResolvedAction resolvedAction = new ResolvedAction(action, arguments);
        return resolvedAction;
    }

    private Map<String, Object> resolveArguments(ActionCall actionCall) {
        Map<String, Object> resolvedArguments = new HashMap<>();

        for (ParameterValue kv : actionCall.getArguments()) {
            String paramName = kv.getParamRef()
                .getName();
            Object value = getParamValue(kv);
            resolvedArguments.put(paramName, value);
        }

        Action action = actionCall.getActionRef();
        ActionArguments parameterList = action.getArguments();

        for (Optimizable optimizable : parameterList.getOptimizables()) {
            String name = optimizable.getName();
            Object value = getOptimizableValue(optimizable);
            resolvedArguments.put(name, value);
        }

        return resolvedArguments;
    }

    private Object getParamValue(ParameterValue kv) {
        Expression expression = kv.getArgument();
        DataType dataType = typeSwitch.doSwitch(expression);
        switch (dataType) {
        case BOOL:
            return expressionCalculator.calculateBoolean(expression);
        case DOUBLE:
            return expressionCalculator.calculateDouble(expression);
        case INT:
            return expressionCalculator.calculateInteger(expression);
        case STRING:
            return expressionCalculator.calculateString(expression);
        default:
            throw new RuntimeException("unsupported expression type: " + dataType);
        }
    }

    private Object getOptimizableValue(Optimizable optimizable) {
        DataType dataType = typeSwitch.doSwitch(optimizable);
        switch (dataType) {
        case BOOL:
            return fieldValueProvider.getBoolValue(optimizable);
        case DOUBLE:
            return fieldValueProvider.getDoubleValue(optimizable);
        case INT:
            return fieldValueProvider.getIntegerValue(optimizable);
        case STRING:
            return fieldValueProvider.getStringValue(optimizable);
        default:
            throw new RuntimeException("unsupported optimization type: " + dataType);
        }
    }
}
