package org.palladiosimulator.simexp.dsl.smodel.interpreter.impl;

import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.ISmodelConfig;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mape.Analyzer;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IfStatement;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;

public class SmodelAnalyzer implements Analyzer {
    private final Smodel model;
    private final IExpressionCalculator expressionCalculator;

    public SmodelAnalyzer(Smodel model, ISmodelConfig smodelConfig, IFieldValueProvider fieldValueProvider) {
        this.model = model;
        this.expressionCalculator = new ExpressionCalculator(smodelConfig, fieldValueProvider);
    }

    @Override
    public boolean analyze() {
        return model.getStatements()
            .stream()
            .anyMatch(statement -> {
                IfStatement ifStatement = (IfStatement) statement;
                if (!ifStatement.getElseStatements()
                    .isEmpty()) {
                    return true;
                }
                Expression condition = ifStatement.getCondition();
                return expressionCalculator.calculateBoolean(condition);
            });
    }

}
