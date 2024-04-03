package org.palladiosimulator.simexp.dsl.smodel.interpreter;

import org.palladiosimulator.simexp.dsl.smodel.smodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Field;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Literal;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Operation;

public class ExpressionCalculator {
    private final IFieldValueProvider fieldValueProvider;

    public ExpressionCalculator(IFieldValueProvider fieldValueProvider) {
        this.fieldValueProvider = fieldValueProvider;
    }

    public boolean calculateBoolean(Expression expression) {
        Field field = expression.getFieldRef();
        if (field != null) {
            return fieldValueProvider.getBoolValue(field);
        }

        Literal literal = expression.getLiteral();
        if (literal != null) {
            BoolLiteral boolLiteral = (BoolLiteral) literal;
            return boolLiteral.isTrue();
        }

        Operation operation = expression.getOp();

        Boolean leftValue = null;
        if (expression.getLeft() != null) {
            leftValue = calculateBoolean(expression.getLeft());
        }

        Boolean rightValue = null;
        if (expression.getRight() != null) {
            rightValue = calculateBoolean(expression.getRight());
        }

        switch (operation) {
        case UNDEFINED:
            return leftValue;

        case OR:
            return leftValue || rightValue;

        case AND:
            return leftValue && rightValue;

        case NOT:
            return !leftValue;

        case EQUAL:
            return leftValue.equals(rightValue);

        case UNEQUAL:
            return !leftValue.equals(rightValue);

        default:
            throw new RuntimeException(
                    "Couldn't determine the value of an expression with operation '" + operation + "'.");
        }
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
}
