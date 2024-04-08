package org.palladiosimulator.simexp.dsl.smodel.interpreter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mape.Analyzer;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mape.Planner;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ActionCall;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.GlobalStatement;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IfStatement;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Statement;
import org.palladiosimulator.simexp.dsl.smodel.smodel.util.SmodelSwitch;

public class SmodelInterpreter extends SmodelSwitch<List<ResolvedAction>> implements Analyzer, Planner {

    private final Smodel model;
    private final ExpressionCalculator expressionCalculator;
    private final IActionCallExecutor actionCallExecutor;

    public SmodelInterpreter(Smodel model, IFieldValueProvider fieldValueProvider) {
        this.model = model;
        IFieldValueProvider saveFieldValueProvider = new SaveFieldValueProvider(fieldValueProvider);
        this.expressionCalculator = new ExpressionCalculator(saveFieldValueProvider);
        this.actionCallExecutor = new ActionCallExecutor(expressionCalculator, saveFieldValueProvider);
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
                return expressionCalculator.calculateBoolean(ifStatement.getCondition());
            });
    }

    @Override
    public List<ResolvedAction> plan() {
        return doSwitch(model);
    }

    @Override
    public List<ResolvedAction> doSwitch(EObject object) {
        if (object == null) {
            throw new RuntimeException("Couldn't interpret an object that is null.");
        }

        List<ResolvedAction> result = super.doSwitch(object);

        if (result == null) {
            throw new RuntimeException("Couldn't interpret an object of class + '" + object.eClass()
                .getName() + "'.");
        }

        return result;
    }

    @Override
    public List<ResolvedAction> caseSmodel(Smodel model) {
        List<ResolvedAction> resolvedActions = new ArrayList<>();

        for (GlobalStatement statement : model.getStatements()) {
            List<ResolvedAction> actions = doSwitch(statement);
            resolvedActions.addAll(actions);
        }

        return resolvedActions;
    }

    @Override
    public List<ResolvedAction> caseIfStatement(IfStatement ifStatement) {
        List<ResolvedAction> resolvedActions = new ArrayList<>();
        Expression ifExpression = ifStatement.getCondition();
        boolean condition = expressionCalculator.calculateBoolean(ifExpression);

        if (condition) {
            for (Statement statement : ifStatement.getThenStatements()) {
                resolvedActions.addAll(doSwitch(statement));
            }
        } else {
            for (Statement statement : ifStatement.getElseStatements()) {
                resolvedActions.addAll(doSwitch(statement));
            }
        }

        return resolvedActions;
    }

    @Override
    public List<ResolvedAction> caseActionCall(ActionCall actionCall) {
        ResolvedAction resolvedAction = actionCallExecutor.execute(actionCall);
        return Collections.singletonList(resolvedAction);
    }
}
