package org.palladiosimulator.simexp.dsl.smodel.interpreter.impl;

import java.util.Objects;

import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DoubleLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Field;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Operation;
import org.palladiosimulator.simexp.dsl.smodel.smodel.StringLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.util.SmodelSwitch;
import org.palladiosimulator.simexp.dsl.smodel.util.SmodelDataTypeSwitch;

public class SmodelDumper extends SmodelSwitch<String> {
    private final IFieldValueProvider fieldValueProvider;

    public SmodelDumper(IFieldValueProvider fieldValueProvider) {
        this.fieldValueProvider = fieldValueProvider;
    }

    @Override
    public String caseExpression(Expression expression) {
        Field field = expression.getFieldRef();
        if (field != null) {
            return getFieldValue(field);
        }

        Expression literal = expression.getLiteral();
        if (literal != null) {
            return getLiteralValue(literal);
        }

        Operation operation = expression.getOp();

        String leftValue = null;
        if (expression.getLeft() != null) {
            leftValue = caseExpression(expression.getLeft());
        }

        String rightValue = null;
        if (expression.getRight() != null) {
            rightValue = caseExpression(expression.getRight());
        }

        switch (operation) {
        case UNDEFINED:
            return leftValue;
        case OR:
            return leftValue + " || " + rightValue;

        case AND:
            return leftValue + " && " + rightValue;

        case NOT:
            return "!" + leftValue;

        case EQUAL:
            return leftValue + " == " + rightValue;

        case UNEQUAL:
            return leftValue + " != " + rightValue;

        case SMALLER:
            return leftValue + " < " + rightValue;

        case SMALLER_OR_EQUAL:
            return leftValue + " <= " + rightValue;

        case GREATER_OR_EQUAL:
            return leftValue + " >= " + rightValue;

        case GREATER:
            return leftValue + " > " + rightValue;

        case PLUS:
            if (rightValue == null) {
                return "+" + leftValue;
            }
            return leftValue + " + " + rightValue;

        case MINUS:
            if (rightValue == null) {
                return "-" + leftValue;
            }
            return leftValue + " - " + rightValue;

        case MULTIPLY:
            return leftValue + " * " + rightValue;

        case DIVIDE:
            return leftValue + " / " + rightValue;

        case MODULO:
            return leftValue + " % " + rightValue;

        default:
            throw new RuntimeException(
                    "Couldn't determine the value of an expression with operation '" + operation + "'.");
        }
    }

    private String getLiteralValue(Expression literal) {
        SmodelDataTypeSwitch smodelDataTypeSwitch = new SmodelDataTypeSwitch();
        DataType dataType = smodelDataTypeSwitch.doSwitch(literal);
        switch (dataType) {
        case BOOL:
            return formatValue(((BoolLiteral) literal).isTrue());
        case DOUBLE:
            return formatValue(((DoubleLiteral) literal).getValue());
        case INT:
            return formatValue(((IntLiteral) literal).getValue());
        case STRING:
            return formatValue(((StringLiteral) literal).getValue());
        }
        throw new RuntimeException("unknown data type: " + dataType);
    }

    private String getFieldValue(Field field) {
        StringBuilder sb = new StringBuilder();
        sb.append("<");
        sb.append(field.getName());
        sb.append(">");
        sb.append(getRawFieldValue(field));
        return sb.toString();
    }

    private String getRawFieldValue(Field field) {
        SmodelDataTypeSwitch smodelDataTypeSwitch = new SmodelDataTypeSwitch();
        DataType dataType = smodelDataTypeSwitch.doSwitch(field);
        switch (dataType) {
        case BOOL:
            return formatValue(fieldValueProvider.getBoolValue(field));
        case DOUBLE:
            return formatValue(fieldValueProvider.getDoubleValue(field));
        case INT:
            return formatValue(fieldValueProvider.getIntegerValue(field));
        case STRING:
            return formatValue(fieldValueProvider.getStringValue(field));
        }
        throw new RuntimeException("unknown data type: " + dataType);
    }

    public String formatValue(Object value) {
        if (value instanceof Double) {
            return formatDouble((Double) value);
        }
        if (value instanceof String) {
            return String.format("\"%s\"", value);
        }
        return Objects.toString(value);
    }

    private String formatDouble(double value) {
        return String.format("%3.20f", value);
    }
}
