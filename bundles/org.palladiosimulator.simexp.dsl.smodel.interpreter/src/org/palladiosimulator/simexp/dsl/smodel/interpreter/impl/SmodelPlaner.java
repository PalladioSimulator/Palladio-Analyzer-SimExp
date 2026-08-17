package org.palladiosimulator.simexp.dsl.smodel.interpreter.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.ISmodelConfig;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.IVariableAssigner;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.ResolvedAction;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.mape.Planner;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ActionCall;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.GlobalStatement;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IfStatement;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Statement;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Variable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.VariableAssignment;
import org.palladiosimulator.simexp.dsl.smodel.smodel.util.SmodelSwitch;

public class SmodelPlaner extends SmodelSwitch<List<ResolvedAction>> implements Planner {
    private static final Logger LOGGER = Logger.getLogger(SmodelPlaner.class);

    private final Smodel model;
    private final IExpressionCalculator expressionCalculator;
    private final IActionCallExecutor actionCallExecutor;
    private final IFieldValueProvider fieldValueProvider;
    private final IVariableAssigner variableAssigner;

    public SmodelPlaner(Smodel model, ISmodelConfig smodelConfig, IFieldValueProvider fieldValueProvider,
            IVariableAssigner variableAssigner) {
        this.model = model;
        this.expressionCalculator = new ExpressionCalculator(smodelConfig, fieldValueProvider);
        this.actionCallExecutor = new ActionCallExecutor(expressionCalculator, fieldValueProvider);
        this.fieldValueProvider = fieldValueProvider;
        this.variableAssigner = variableAssigner;
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

    @Override
    public List<ResolvedAction> caseVariableAssignment(VariableAssignment variableAssignment) {
        SmodelDumper dumper = new SmodelDumper(fieldValueProvider);
        StringBuilder sb = new StringBuilder();
        Level assignmentLogLevel = Level.DEBUG;
        if (logAssignments(assignmentLogLevel)) {
            sb.append("assign ");
            Variable variableRef = variableAssignment.getVariableRef();
            sb.append(variableRef.getName());
            sb.append(" = ");
            Expression value = variableAssignment.getValue();
            sb.append(dumper.doSwitch(value));
        }
        Object assignedValue = variableAssigner.assign(variableAssignment);
        if (logAssignments(assignmentLogLevel)) {
            sb.append(" := ");
            sb.append(dumper.formatValue(assignedValue));
            LOGGER.log(assignmentLogLevel, sb.toString());
        }
        return Collections.emptyList();
    }

    private boolean logAssignments(Level level) {
        return LOGGER.isEnabledFor(level);
    }
}
