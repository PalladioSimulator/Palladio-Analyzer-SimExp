package org.palladiosimulator.simexp.dsl.smodel.interpreter.impl;

import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;

public interface IExpressionCalculator {

    boolean calculateBoolean(Expression expression);

    int calculateInteger(Expression expression);

    double calculateDouble(Expression expression);

    String calculateString(Expression expression);

}