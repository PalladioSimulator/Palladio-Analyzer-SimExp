package org.palladiosimulator.simexp.dsl.smodel.interpreter.value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.ISmodelConfig;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Constant;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Operation;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelFactory;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

public class ConstantValueProviderTest {
    private static final double DOUBLE_EPSILON = 1e-15;

    private ConstantValueProvider provider;

    private SmodelCreator smodelCreator;
    @Mock
    private ISmodelConfig smodelConfig;

    public ConstantValueProviderTest() {
        initMocks(this);
        when(smodelConfig.getEpsilon()).thenReturn(DOUBLE_EPSILON);
        smodelCreator = new SmodelCreator();
        provider = new ConstantValueProvider(smodelConfig);
    }

    @Test
    public void boolLiteral() {
        Constant constant = smodelCreator.createConstant("const", DataType.BOOL, smodelCreator.createBoolLiteral(true));

        Boolean actualValue = provider.getBoolValue(constant);

        assertThat(actualValue).isTrue();
    }

    @Test
    public void boolConstant() {
        Constant constant1 = smodelCreator.createConstant("const1", DataType.BOOL,
                smodelCreator.createBoolLiteral(true));
        Constant constant2 = smodelCreator.createConstant("const2", DataType.BOOL, null);
        Expression fieldExpression2 = SmodelFactory.eINSTANCE.createExpression();
        fieldExpression2.setFieldRef(constant1);
        constant2.setValue(fieldExpression2);

        Boolean actualValue = provider.getBoolValue(constant2);

        assertThat(actualValue).isTrue();
    }

    @Test
    public void intLiteral() {
        Constant constant = smodelCreator.createConstant("const", DataType.INT, smodelCreator.createIntLiteral(1));

        Integer actualValue = provider.getIntegerValue(constant);

        assertThat(actualValue).isEqualTo(1);
    }

    @Test
    public void intCalculated1() {
        Constant constant = smodelCreator.createConstant("const", DataType.INT, null);
        Expression expression = SmodelFactory.eINSTANCE.createExpression();
        expression.setOp(Operation.PLUS);
        Expression expressionLeft = SmodelFactory.eINSTANCE.createExpression();
        expressionLeft.setLiteral(smodelCreator.createIntLiteral(1));
        expression.setLeft(expressionLeft);
        Expression expressionRight = SmodelFactory.eINSTANCE.createExpression();
        expressionRight.setLiteral(smodelCreator.createIntLiteral(2));
        expression.setRight(expressionRight);
        constant.setValue(expression);

        Integer actualValue = provider.getIntegerValue(constant);

        assertThat(actualValue).isEqualTo(3);
    }

    @Test
    public void intCalculated2() {
        Constant constant1 = smodelCreator.createConstant("const1", DataType.INT, smodelCreator.createIntLiteral(1));
        Constant constant2 = smodelCreator.createConstant("const2", DataType.INT, smodelCreator.createIntLiteral(2));
        Constant constant = smodelCreator.createConstant("const", DataType.INT, null);
        Expression expression = SmodelFactory.eINSTANCE.createExpression();
        expression.setOp(Operation.PLUS);
        Expression expressionLeft = SmodelFactory.eINSTANCE.createExpression();
        expressionLeft.setFieldRef(constant1);
        expression.setLeft(expressionLeft);
        Expression expressionRight = SmodelFactory.eINSTANCE.createExpression();
        expressionRight.setFieldRef(constant2);
        expression.setRight(expressionRight);
        constant.setValue(expression);

        Integer actualValue = provider.getIntegerValue(constant);

        assertThat(actualValue).isEqualTo(3);
    }

    @Test
    public void doubleLiteral() {
        Constant constant = smodelCreator.createConstant("const", DataType.DOUBLE,
                smodelCreator.createDoubleLiteral(1.0));

        Double actualValue = provider.getDoubleValue(constant);

        assertThat(actualValue).isEqualTo(1.0, offset(DOUBLE_EPSILON));
    }

    @Test
    public void stringLiteral() {
        Constant constant = smodelCreator.createConstant("const", DataType.STRING,
                smodelCreator.createStringLiteral("s"));

        String actualValue = provider.getStringValue(constant);

        assertThat(actualValue).isEqualTo("s");
    }
}
