package org.palladiosimulator.simexp.dsl.smodel.interpreter.impl;

import org.apache.commons.math3.util.Precision;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.ISmodelConfig;
import org.palladiosimulator.simexp.dsl.smodel.smodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DoubleLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Field;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Literal;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Operation;
import org.palladiosimulator.simexp.dsl.smodel.smodel.StringLiteral;
import org.palladiosimulator.simexp.dsl.smodel.util.SmodelDataTypeSwitch;

public class ExpressionCalculator implements IExpressionCalculator {
    private final double epsilon;
    private final IFieldValueProvider fieldValueProvider;

    public ExpressionCalculator(ISmodelConfig smodelConfig, IFieldValueProvider fieldValueProvider) {
        this.epsilon = smodelConfig.getEpsilon();
        this.fieldValueProvider = fieldValueProvider;
    }

    @Override
    public double getEpsilon() {
        return epsilon;
    }

    @Override
    public boolean calculateBoolean(Expression expression) {
        Object value = caseExpression(expression);
        return (boolean) value;
    }

    @Override
    public int calculateInteger(Expression expression) {
        Object value = caseExpression(expression);
        Number numberValue = (Number) value;
        return numberValue.intValue();
    }

    @Override
    public double calculateDouble(Expression expression) {
        Object value = caseExpression(expression);
        Number numberValue = (Number) value;
        return numberValue.doubleValue();
    }

    @Override
    public String calculateString(Expression expression) {
        Field field = expression.getFieldRef();
        if (field != null) {
            String fieldValue = (String) getFieldValue(field);
            return fieldValue;
        }

        StringLiteral stringLiteral = (StringLiteral) expression.getLiteral();
        return stringLiteral.getValue();
    }

    private Object caseExpression(Expression expression) {
        if (expression instanceof Literal literal) {
            return getLiteralValue(literal);
        }

        Field field = expression.getFieldRef();
        if (field != null) {
            return getFieldValue(field);
        }

        Expression literal = expression.getLiteral();
        if (literal != null) {
            return getLiteralValue(literal);
        }

        Operation operation = expression.getOp();

        Object leftValue = null;
        if (expression.getLeft() != null) {
            leftValue = caseExpression(expression.getLeft());
        }

        Object rightValue = null;
        if (expression.getRight() != null) {
            rightValue = caseExpression(expression.getRight());
        }

        switch (operation) {
        case UNDEFINED:
            return leftValue;

        case OR:
            return (Boolean) leftValue || (Boolean) rightValue;

        case AND:
            return (Boolean) leftValue && (Boolean) rightValue;

        case NOT:
            return !(Boolean) leftValue;

        case EQUAL:
            return isEqual(leftValue, rightValue);

        case UNEQUAL:
            return !isEqual(leftValue, rightValue);

        case SMALLER:
            return ((Number) leftValue).doubleValue() < ((Number) rightValue).doubleValue();

        case SMALLER_OR_EQUAL:
            return ((Number) leftValue).doubleValue() <= ((Number) rightValue).doubleValue();

        case GREATER_OR_EQUAL:
            return ((Number) leftValue).doubleValue() >= ((Number) rightValue).doubleValue();

        case GREATER:
            return ((Number) leftValue).doubleValue() > ((Number) rightValue).doubleValue();

        case PLUS:
            if (rightValue == null) {
                return +((Number) leftValue).doubleValue();
            }
            return ((Number) leftValue).doubleValue() + ((Number) rightValue).doubleValue();

        case MINUS:
            if (rightValue == null) {
                return -((Number) leftValue).doubleValue();
            }
            return ((Number) leftValue).doubleValue() - ((Number) rightValue).doubleValue();

        case MULTIPLY:
            return ((Number) leftValue).doubleValue() * ((Number) rightValue).doubleValue();

        case DIVIDE:
            return divide((Number) leftValue, (Number) rightValue);

        case MODULO:
            return ((Number) leftValue).doubleValue() % ((Number) rightValue).doubleValue();

        default:
            throw new RuntimeException(
                    "Couldn't determine the value of an expression with operation '" + operation + "'.");
        }
    }

    private boolean isEqual(Object left, Object right) {
        if ((left instanceof Double) || (right instanceof Double)) {
            Number leftDouble = (Number) left;
            Number rightDouble = (Number) right;
            return Precision.compareTo(leftDouble.doubleValue(), rightDouble.doubleValue(), epsilon) == 0;
        }
        return left.equals(right);
    }

    private Number divide(Number divident, Number divisor) {
        if ((divident instanceof Double) || (divisor instanceof Double)) {
            Double doubleDivident = divident.doubleValue();
            Double doubleDivisor = divisor.doubleValue();
            return doubleDivident / doubleDivisor;
        }
        Integer intDivident = (Integer) divident;
        Integer intDivisor = (Integer) divisor;
        Integer result = intDivident / intDivisor;
        return result.doubleValue();
    }

    private Object getLiteralValue(Expression literal) {
        SmodelDataTypeSwitch smodelDataTypeSwitch = new SmodelDataTypeSwitch();
        DataType dataType = smodelDataTypeSwitch.doSwitch(literal);
        switch (dataType) {
        case BOOL:
            return ((BoolLiteral) literal).isTrue();
        case DOUBLE:
            return ((DoubleLiteral) literal).getValue();
        case INT:
            return ((IntLiteral) literal).getValue();
        case STRING:
            return ((StringLiteral) literal).getValue();
        }
        throw new RuntimeException("unknown data type: " + dataType);
    }

    private Object getFieldValue(Field field) {
        SmodelDataTypeSwitch smodelDataTypeSwitch = new SmodelDataTypeSwitch();
        DataType dataType = smodelDataTypeSwitch.doSwitch(field);
        switch (dataType) {
        case BOOL:
            return fieldValueProvider.getBoolValue(field);
        case DOUBLE:
            return fieldValueProvider.getDoubleValue(field);
        case INT:
            return fieldValueProvider.getIntegerValue(field);
        case STRING:
            return fieldValueProvider.getStringValue(field);
        }
        throw new RuntimeException("unknown data type: " + dataType);
    }
}
