package org.palladiosimulator.simexp.dsl.smodel.interpreter.value.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.ISmodelConfig;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Constant;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Expression;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelFactory;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Variable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.VariableAssignment;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

public class VariableValueProviderTest {
    private static final double DOUBLE_EPSILON = 1e-15;

    private VariableValueProvider provider;

    @Mock
    private ISmodelConfig smodelConfig;
    @Mock
    private IFieldValueProvider constantValueProvider;
    @Mock
    private IFieldValueProvider probeValueProvider;
    @Mock
    private IFieldValueProvider optimizableValueProvider;
    @Mock
    private IFieldValueProvider envVariableValueProvider;

    private SmodelCreator smodelCreator;

    public VariableValueProviderTest() {
        initMocks(this);
        when(smodelConfig.getEpsilon()).thenReturn(DOUBLE_EPSILON);
        smodelCreator = new SmodelCreator();
        provider = new VariableValueProvider(smodelConfig, constantValueProvider, probeValueProvider,
                optimizableValueProvider, envVariableValueProvider);
    }

    @Test
    public void boolLiteral() {
        Variable variable = smodelCreator.createVariable("variable", DataType.BOOL,
                smodelCreator.createBoolLiteral(true));

        Boolean actualValue = provider.getBoolValue(variable);

        assertThat(actualValue).isTrue();
    }

    @Test
    public void boolConstant() {
        Constant constant = smodelCreator.createConstant("const", DataType.BOOL, smodelCreator.createBoolLiteral(true));
        when(constantValueProvider.getBoolValue(constant)).thenReturn(true);
        Variable variable = smodelCreator.createVariable("variable", DataType.BOOL, null);
        Expression fieldExpression = SmodelFactory.eINSTANCE.createExpression();
        fieldExpression.setFieldRef(constant);
        variable.setValue(fieldExpression);

        Boolean actualValue = provider.getBoolValue(variable);

        assertThat(actualValue).isTrue();
    }

    @Test
    public void boolLookup() {
        Constant constant = smodelCreator.createConstant("const", DataType.BOOL, smodelCreator.createBoolLiteral(true));
        when(constantValueProvider.getBoolValue(constant)).thenReturn(true);
        Variable variable1 = smodelCreator.createVariable("variable", DataType.BOOL, null);
        Expression fieldExpression = SmodelFactory.eINSTANCE.createExpression();
        fieldExpression.setFieldRef(constant);
        variable1.setValue(fieldExpression);
        Variable variable2 = smodelCreator.createVariable("variable", DataType.BOOL, null);
        provider.getBoolValue(variable1);

        Boolean actualValue = provider.getBoolValue(variable2);

        assertThat(actualValue).isTrue();
        verify(constantValueProvider, times(1)).getBoolValue(constant);
    }

    @Test
    public void intLiteral() {
        Variable variable = smodelCreator.createVariable("variable", DataType.INT, smodelCreator.createIntLiteral(1));

        Integer actualValue = provider.getIntegerValue(variable);

        assertThat(actualValue).isEqualTo(1);
    }

    @Test
    public void doubleLiteral() {
        Variable variable = smodelCreator.createVariable("variable", DataType.DOUBLE,
                smodelCreator.createDoubleLiteral(1.0));

        Double actualValue = provider.getDoubleValue(variable);

        assertThat(actualValue).isEqualTo(1.0, offset(DOUBLE_EPSILON));
    }

    @Test
    public void stringLiteral() {
        Variable variable = smodelCreator.createVariable("variable", DataType.STRING,
                smodelCreator.createStringLiteral("s"));

        String actualValue = provider.getStringValue(variable);

        assertThat(actualValue).isEqualTo("s");
    }

    @Test
    public void boolAssignLiteral() {
        Variable variable = smodelCreator.createVariable("variable", DataType.BOOL,
                smodelCreator.createBoolLiteral(false));
        provider.getBoolValue(variable);
        Expression expression = smodelCreator.createLiteralBoolExpression(true);
        VariableAssignment variableAssignment = smodelCreator.createVariableAssignment(variable, expression);

        provider.assign(variableAssignment);

        Boolean actualValue = provider.getBoolValue(variable);
        assertThat(actualValue).isTrue();
    }

    @Test
    public void resetBoolLiteral() {
        Variable variable = smodelCreator.createVariable("variable", DataType.BOOL,
                smodelCreator.createBoolLiteral(true));
        provider.getBoolValue(variable);
        VariableAssignment variableAssignment = smodelCreator.createVariableAssignment(variable,
                smodelCreator.createLiteralBoolExpression(false));
        provider.assign(variableAssignment);

        provider.reset();

        Boolean actualValue = provider.getBoolValue(variable);
        assertThat(actualValue).isTrue();
    }

    @Test
    public void resetIntLiteral() {
        Variable variable = smodelCreator.createVariable("variable", DataType.INT, smodelCreator.createIntLiteral(1));
        provider.getIntegerValue(variable);
        VariableAssignment variableAssignment = smodelCreator.createVariableAssignment(variable,
                smodelCreator.createLiteralIntExpression(2));
        provider.assign(variableAssignment);

        provider.reset();

        Integer actualValue = provider.getIntegerValue(variable);
        assertThat(actualValue).isEqualTo(1);
    }

    @Test
    public void resetNoGetIntLiteral() {
        Variable variable = smodelCreator.createVariable("variable", DataType.INT, smodelCreator.createIntLiteral(1));
        VariableAssignment variableAssignment = smodelCreator.createVariableAssignment(variable,
                smodelCreator.createLiteralIntExpression(2));
        provider.assign(variableAssignment);

        provider.reset();

        Integer actualValue = provider.getIntegerValue(variable);
        assertThat(actualValue).isEqualTo(1);
    }
}
