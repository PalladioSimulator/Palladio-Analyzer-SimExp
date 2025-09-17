package org.palladiosimulator.simexp.dsl.smodel.api;

import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;

public interface IExpressionCalculator {
    IPrecisionProvider getPrecisionProvider();

    boolean calculateBoolean(Expression expression);

    int calculateInteger(Expression expression);

    double calculateDouble(Expression expression);

    String calculateString(Expression expression);

}