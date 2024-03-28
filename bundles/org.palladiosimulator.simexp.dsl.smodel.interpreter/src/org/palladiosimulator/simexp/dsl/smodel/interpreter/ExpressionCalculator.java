package org.palladiosimulator.simexp.dsl.smodel.interpreter;

import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;

public class ExpressionCalculator {
    private final IFieldValueProvider fieldValueProvider;

    public ExpressionCalculator(IFieldValueProvider fieldValueProvider) {
        this.fieldValueProvider = fieldValueProvider;
    }

    public boolean calculateBoolean(Expression expression) {
        return false;
    }

    public int calculateInteger(Expression expression) {
        return 0;
    }

    public double calculateDouble(Expression expression) {
        return 0.0;
    }

    public String calculateString(Expression expression) {
        return null;
    }
}
