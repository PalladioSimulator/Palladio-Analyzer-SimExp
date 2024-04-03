package org.palladiosimulator.simexp.dsl.smodel.interpreter;

import org.palladiosimulator.simexp.dsl.smodel.smodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Field;
import org.palladiosimulator.simexp.dsl.smodel.smodel.FloatLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Literal;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Operation;
import org.palladiosimulator.simexp.dsl.smodel.smodel.StringLiteral;
import org.palladiosimulator.simexp.dsl.smodel.util.SmodelDataTypeSwitch;

public class ExpressionCalculator {
    private final IFieldValueProvider fieldValueProvider;

    public ExpressionCalculator(IFieldValueProvider fieldValueProvider) {
        this.fieldValueProvider = fieldValueProvider;
    }

    public boolean calculateBoolean(Expression expression) {
        Object value = caseExpression(expression);
        return (boolean) value;
    }

    public int calculateInteger(Expression expression) {
        // return (int) calculate(expression);
        return 0;
    }

    public double calculateDouble(Expression expression) {
        // return (double) calculate(expression);
        return 0;
    }

    public String calculateString(Expression expression) {
        // return (String) calculate(expression);
        return null;
    }

    private Object caseExpression(Expression expression) {
        Field field = expression.getFieldRef();
        if (field != null) {
            return getFieldValue(field);
        }

        Literal literal = expression.getLiteral();
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
            return leftValue.equals(rightValue);

        case UNEQUAL:
            return !leftValue.equals(rightValue);

        case SMALLER:
            return ((Number) leftValue).floatValue() < ((Number) rightValue).floatValue();

        case SMALLER_OR_EQUAL:
            return ((Number) leftValue).floatValue() <= ((Number) rightValue).floatValue();

        case GREATER_OR_EQUAL:
            return ((Number) leftValue).floatValue() >= ((Number) rightValue).floatValue();

        case GREATER:
            return ((Number) leftValue).floatValue() > ((Number) rightValue).floatValue();

        case PLUS:
            if (rightValue == null) {
                return +((Number) leftValue).floatValue();

            } else {
                return ((Number) leftValue).floatValue() + ((Number) rightValue).floatValue();
            }

        case MINUS:
            if (rightValue == null) {
                return -((Number) leftValue).floatValue();

            } else {
                return ((Number) leftValue).floatValue() - ((Number) rightValue).floatValue();
            }

        case MULTIPLY:
            return ((Number) leftValue).floatValue() * ((Number) rightValue).floatValue();

        case DIVIDE:
            return ((Number) leftValue).floatValue() / ((Number) rightValue).floatValue();

        case MODULO:
            return ((Number) leftValue).floatValue() % ((Number) rightValue).floatValue();

        default:
            throw new RuntimeException(
                    "Couldn't determine the value of an expression with operation '" + operation + "'.");
        }
    }

    private Object getLiteralValue(Literal literal) {
        SmodelDataTypeSwitch smodelDataTypeSwitch = new SmodelDataTypeSwitch();
        DataType dataType = smodelDataTypeSwitch.doSwitch(literal);
        switch (dataType) {
        case BOOL:
            return ((BoolLiteral) literal).isTrue();
        case FLOAT:
            return ((FloatLiteral) literal).getValue();
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
        case FLOAT:
            return fieldValueProvider.getDoubleValue(field);
        case INT:
            return fieldValueProvider.getIntegerValue(field);
        case STRING:
            return fieldValueProvider.getStringValue(field);
        }
        throw new RuntimeException("unknown data type: " + dataType);
    }
}
