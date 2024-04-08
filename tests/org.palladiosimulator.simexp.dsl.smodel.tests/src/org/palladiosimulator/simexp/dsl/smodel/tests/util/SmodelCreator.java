package org.palladiosimulator.simexp.dsl.smodel.tests.util;

import org.palladiosimulator.simexp.dsl.smodel.smodel.Action;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ActionArguments;
import org.palladiosimulator.simexp.dsl.smodel.smodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Bounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Constant;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DoubleLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.EnvVariable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Literal;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Parameter;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ProbeAdressingKind;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelFactory;
import org.palladiosimulator.simexp.dsl.smodel.smodel.StringLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Variable;

public class SmodelCreator {
    public Variable createVariable(String name, DataType type, Literal literal) {
        Variable variable = SmodelFactory.eINSTANCE.createVariable();
        variable.setName(name);
        variable.setDataType(type);
        if (literal != null) {
            Expression expression = SmodelFactory.eINSTANCE.createExpression();
            expression.setLiteral(literal);
            variable.setValue(expression);
        }
        return variable;
    }

    public Expression createLiteralBoolExpression(boolean value) {
        Expression literalExpression = SmodelFactory.eINSTANCE.createExpression();
        BoolLiteral expectedLiteral = createBoolLiteral(value);
        literalExpression.setLiteral(expectedLiteral);
        return literalExpression;
    }

    public Expression createLiteralIntExpression(int value) {
        Expression literalExpression = SmodelFactory.eINSTANCE.createExpression();
        IntLiteral literal = createIntLiteral(value);
        literalExpression.setLiteral(literal);
        return literalExpression;
    }

    public Expression createLiteralDoubleExpression(double value) {
        Expression literalExpression = SmodelFactory.eINSTANCE.createExpression();
        DoubleLiteral literal = createDoubleLiteral(value);
        literalExpression.setLiteral(literal);
        return literalExpression;
    }

    public SetBounds createSetBounds(Literal... values) {
        SetBounds bounds = SmodelFactory.eINSTANCE.createSetBounds();
        for (Literal value : values) {
            bounds.getValues()
                .add(value);
        }
        return bounds;
    }

    public RangeBounds createRangeBounds(Literal start, Literal end, Literal step) {
        RangeBounds bounds = SmodelFactory.eINSTANCE.createRangeBounds();
        bounds.setStartValue(start);
        bounds.setEndValue(end);
        bounds.setStepSize(step);
        return bounds;
    }

    public Optimizable createOptimizable(String name, DataType type, Bounds bounds) {
        Optimizable optimizable = SmodelFactory.eINSTANCE.createOptimizable();
        optimizable.setName(name);
        optimizable.setDataType(type);
        optimizable.setValues(bounds);
        return optimizable;
    }

    public EnvVariable createEnvVariable(String name, DataType type, String id) {
        EnvVariable variable = SmodelFactory.eINSTANCE.createEnvVariable();
        variable.setName(name);
        variable.setDataType(type);
        variable.setVariableId(id);
        return variable;
    }

    public Parameter createParameter(String name, DataType type) {
        Parameter parameter = SmodelFactory.eINSTANCE.createParameter();
        parameter.setName(name);
        parameter.setDataType(type);
        return parameter;
    }

    public Action createAction(String name) {
        Action action = SmodelFactory.eINSTANCE.createAction();
        action.setName(name);
        ActionArguments actionArguments = SmodelFactory.eINSTANCE.createActionArguments();
        action.setArguments(actionArguments);
        return action;
    }

    public Constant createConstant(String name, DataType type, Literal literal) {
        Constant constant = SmodelFactory.eINSTANCE.createConstant();
        constant.setName(name);
        constant.setDataType(type);
        Expression expression = SmodelFactory.eINSTANCE.createExpression();
        expression.setLiteral(literal);
        constant.setValue(expression);
        return constant;
    }

    public Probe createProbe(String name, DataType type, ProbeAdressingKind kind, String id) {
        Probe probe = SmodelFactory.eINSTANCE.createProbe();
        probe.setName(name);
        probe.setDataType(type);
        probe.setKind(kind);
        probe.setIdentifier(id);
        return probe;
    }

    public BoolLiteral createBoolLiteral(boolean value) {
        BoolLiteral literal = SmodelFactory.eINSTANCE.createBoolLiteral();
        literal.setTrue(value);
        return literal;
    }

    public IntLiteral createIntLiteral(int value) {
        IntLiteral literal = SmodelFactory.eINSTANCE.createIntLiteral();
        literal.setValue(value);
        return literal;
    }

    public DoubleLiteral createDoubleLiteral(double value) {
        DoubleLiteral literal = SmodelFactory.eINSTANCE.createDoubleLiteral();
        literal.setValue(value);
        return literal;
    }

    public StringLiteral createStringLiteral(String value) {
        StringLiteral literal = SmodelFactory.eINSTANCE.createStringLiteral();
        literal.setValue(value);
        return literal;
    }
}
