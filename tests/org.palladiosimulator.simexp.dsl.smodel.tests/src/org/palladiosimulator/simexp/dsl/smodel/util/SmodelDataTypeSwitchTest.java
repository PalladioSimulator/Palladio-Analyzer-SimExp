package org.palladiosimulator.simexp.dsl.smodel.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Literal;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Operation;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Parameter;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ProbeAdressingKind;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelFactory;
import org.palladiosimulator.simexp.dsl.smodel.tests.util.SmodelCreator;

public class SmodelDataTypeSwitchTest {

    private SmodelDataTypeSwitch typeSwitch;
    private SmodelCreator smodelCreator;

    @Before
    public void setUp() {
        smodelCreator = new SmodelCreator();
        typeSwitch = new SmodelDataTypeSwitch();
    }

    @Test
    public void literalIntType() throws Exception {
        IntLiteral literal = smodelCreator.createIntLiteral(0);

        DataType actualDataType = typeSwitch.doSwitch(literal);

        assertThat(actualDataType).isEqualTo(DataType.INT);
    }

    @Test
    public void probeType() throws Exception {
        Probe probe = smodelCreator.createProbe("probe", DataType.BOOL, ProbeAdressingKind.ID, "someId");

        DataType actualDataType = typeSwitch.doSwitch(probe);

        assertThat(actualDataType).isEqualTo(DataType.BOOL);
    }

    @Test
    public void parameterType() throws Exception {
        Parameter parameter = smodelCreator.createParameter("d", DataType.DOUBLE);

        DataType actualDataType = typeSwitch.doSwitch(parameter);

        assertThat(actualDataType).isEqualTo(DataType.DOUBLE);
    }

    @Test
    public void expressionTypeFieldRef() throws Exception {
        Probe probe = smodelCreator.createProbe("probe", DataType.STRING, ProbeAdressingKind.ID, "someId");
        Expression expression = SmodelFactory.eINSTANCE.createExpression();
        expression.setFieldRef(probe);

        DataType actualDataType = typeSwitch.doSwitch(expression);

        assertThat(actualDataType).isEqualTo(DataType.STRING);
    }

    @Test
    public void expressionTypeLiteral() throws Exception {
        Literal literal = smodelCreator.createStringLiteral("");
        Expression expression = SmodelFactory.eINSTANCE.createExpression();
        expression.setLiteral(literal);

        DataType actualDataType = typeSwitch.doSwitch(expression);

        assertThat(actualDataType).isEqualTo(DataType.STRING);
    }

    @Test
    public void expressionTypeUnaryBool() throws Exception {
        Expression left = smodelCreator.createLiteralBoolExpression(true);
        Expression expression = createBinaryExpression(Operation.NOT, left, null);

        DataType actualDataType = typeSwitch.doSwitch(expression);

        assertThat(actualDataType).isEqualTo(DataType.BOOL);
    }

    @Test
    public void expressionTypeBinaryBool() throws Exception {
        Expression left = smodelCreator.createLiteralBoolExpression(true);
        Expression right = smodelCreator.createLiteralBoolExpression(false);
        Expression expression = createBinaryExpression(Operation.OR, left, right);

        DataType actualDataType = typeSwitch.doSwitch(expression);

        assertThat(actualDataType).isEqualTo(DataType.BOOL);
    }

    @Test
    public void expressionTypeBinaryInt() throws Exception {
        Expression left = smodelCreator.createLiteralIntExpression(0);
        Expression right = smodelCreator.createLiteralIntExpression(0);
        Expression expression = createBinaryExpression(Operation.PLUS, left, right);

        DataType actualDataType = typeSwitch.doSwitch(expression);

        assertThat(actualDataType).isEqualTo(DataType.INT);
    }

    @Test
    public void expressionTypeBinaryDouble() throws Exception {
        Expression left = smodelCreator.createLiteralDoubleExpression(0.0);
        Expression right = smodelCreator.createLiteralDoubleExpression(0.0);
        Expression expression = createBinaryExpression(Operation.PLUS, left, right);

        DataType actualDataType = typeSwitch.doSwitch(expression);

        assertThat(actualDataType).isEqualTo(DataType.DOUBLE);
    }

    @Test
    public void expressionTypeBinaryModulo() throws Exception {
        Expression left = smodelCreator.createLiteralIntExpression(0);
        Expression right = smodelCreator.createLiteralIntExpression(0);
        Expression expression = createBinaryExpression(Operation.MODULO, left, right);

        DataType actualDataType = typeSwitch.doSwitch(expression);

        assertThat(actualDataType).isEqualTo(DataType.INT);
    }

    private Expression createBinaryExpression(Operation op, Expression left, Expression right) {
        Expression expression = SmodelFactory.eINSTANCE.createExpression();
        expression.setOp(op);
        expression.setLeft(left);
        expression.setRight(right);
        return expression;
    }
}
