package org.palladiosimulator.simexp.dsl.smodel.interpreter.value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.ExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Variable;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

public class VariableValueProviderTest {
    private VariableValueProvider provider;

    @Mock
    private ExpressionCalculator expressionCalculator;

    private SmodelCreator smodelCreator;

    public VariableValueProviderTest() {
        initMocks(this);
        smodelCreator = new SmodelCreator();
        provider = new VariableValueProvider(expressionCalculator);
    }

    @Test
    public void boolLiteral() {
        Variable variable = smodelCreator.createVariable("const", DataType.BOOL, smodelCreator.createBoolLiteral(true));
        when(expressionCalculator.calculateBoolean(variable.getValue())).thenReturn(true);

        Boolean actualValue = provider.getBoolValue(variable);

        assertThat(actualValue).isTrue();
    }
}
