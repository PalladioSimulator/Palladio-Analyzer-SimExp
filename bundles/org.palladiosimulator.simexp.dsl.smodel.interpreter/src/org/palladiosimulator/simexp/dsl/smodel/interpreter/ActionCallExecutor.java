package org.palladiosimulator.simexp.dsl.smodel.interpreter;

import java.util.HashMap;
import java.util.Map;

import org.palladiosimulator.simexp.dsl.smodel.smodel.Action;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ActionCall;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ArgumentKeyValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ParameterList;
import org.palladiosimulator.simexp.dsl.smodel.util.SmodelDataTypeSwitch;

public class ActionCallExecutor implements IActionCallExecutor {
    private final SmodelDataTypeSwitch typeSwitch;
    private final ExpressionCalculator expressionCalculator;
    private final IFieldValueProvider fieldValueProvider;

    public ActionCallExecutor(ExpressionCalculator expressionCalculator, IFieldValueProvider fieldValueProvider) {
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

        for (ArgumentKeyValue kv : actionCall.getArguments()) {
            String paramName = kv.getParamRef()
                .getName();
            Object value = getParamValue(kv);
            resolvedArguments.put(paramName, value);
        }

        Action action = actionCall.getActionRef();
        ParameterList parameterList = action.getParameterList();

        for (Optimizable optimizable : parameterList.getVariables()) {
            String name = optimizable.getName();
            Object value = getOptimizableValue(optimizable);
            resolvedArguments.put(name, value);
        }

        return resolvedArguments;
    }

    private Object getParamValue(ArgumentKeyValue kv) {
        Expression expression = kv.getArgument();
        DataType dataType = typeSwitch.doSwitch(expression);
        switch (dataType) {
        case BOOL:
            return expressionCalculator.calculateBoolean(expression);
        case FLOAT:
            return expressionCalculator.calculateFloat(expression);
        case INT:
            return expressionCalculator.calculateInteger(expression);
        default:
            throw new RuntimeException("unsupported expression type: " + dataType);
        }
    }

    private Object getOptimizableValue(Optimizable optimizable) {
        DataType datatype = typeSwitch.doSwitch(optimizable);
        switch (datatype) {
        case BOOL:
            return fieldValueProvider.getBoolValue(optimizable);
        case FLOAT:
            return fieldValueProvider.getDoubleValue(optimizable);
        case INT:
            return fieldValueProvider.getIntegerValue(optimizable);
        case STRING:
            return fieldValueProvider.getStringValue(optimizable);
        default:
            throw new RuntimeException("");
        }
    }
}
