package org.palladiosimulator.simexp.dsl.smodel.interpreter.value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.ExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Constant;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelFactory;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

public class ConstantValueProviderTest {
    private static final double DOUBLE_EPSILON = 1e-15;

    private ConstantValueProvider provider;

    private SmodelCreator smodelCreator;

    @Mock
    private ExpressionCalculator expressionCalculator;

    public ConstantValueProviderTest() {
        initMocks(this);
        smodelCreator = new SmodelCreator();
        provider = new ConstantValueProvider();
    }

    @Test
    public void boolLiteral() {
        Constant constant = smodelCreator.createConstant("const", DataType.BOOL, smodelCreator.createBoolLiteral(true));
        when(expressionCalculator.calculateBoolean(constant.getValue())).thenReturn(true);

        Boolean actualValue = provider.getBoolValue(constant);

        assertThat(actualValue).isTrue();
    }

    @Test
    public void boolConstant() {
        Constant constant1 = smodelCreator.createConstant("const1", DataType.BOOL,
                smodelCreator.createBoolLiteral(true));
        Constant constant2 = smodelCreator.createConstant("const2", DataType.BOOL, null);
        Expression expectedExpression2 = SmodelFactory.eINSTANCE.createExpression();
        expectedExpression2.setFieldRef(constant1);
        constant2.setValue(expectedExpression2);
        when(expressionCalculator.calculateBoolean(constant1.getValue())).thenReturn(true);

        Boolean actualValue = provider.getBoolValue(constant2);

        assertThat(actualValue).isTrue();
    }

    @Test
    public void intLiteral() {
        Constant constant = smodelCreator.createConstant("const", DataType.INT, smodelCreator.createIntLiteral(1));
        when(expressionCalculator.calculateBoolean(constant.getValue())).thenReturn(true);

        Integer actualValue = provider.getIntegerValue(constant);

        assertThat(actualValue).isEqualTo(1);
    }

    @Test
    public void doubleLiteral() {
        Constant constant = smodelCreator.createConstant("const", DataType.DOUBLE,
                smodelCreator.createDoubleLiteral(1.0));
        when(expressionCalculator.calculateBoolean(constant.getValue())).thenReturn(true);

        Double actualValue = provider.getDoubleValue(constant);

        assertThat(actualValue).isEqualTo(1.0, offset(DOUBLE_EPSILON));
    }

    @Test
    public void stringLiteral() {
        Constant constant = smodelCreator.createConstant("const", DataType.STRING,
                smodelCreator.createStringLiteral("s"));
        when(expressionCalculator.calculateBoolean(constant.getValue())).thenReturn(true);

        String actualValue = provider.getStringValue(constant);

        assertThat(actualValue).isEqualTo("s");
    }
}
