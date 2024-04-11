package org.palladiosimulator.simexp.dsl.smodel.interpreter.value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.IFieldValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Variable;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

public class VariableValueProviderTest {
    private static final double DOUBLE_EPSILON = 1e-15;

    private VariableValueProvider provider;

    @Mock
    private IFieldValueProvider fieldValueProvider;

    private SmodelCreator smodelCreator;

    public VariableValueProviderTest() {
        initMocks(this);
        smodelCreator = new SmodelCreator();
        provider = new VariableValueProvider(fieldValueProvider);
    }

    @Test
    public void boolLiteral() {
        Variable variable = smodelCreator.createVariable("variable", DataType.BOOL,
                smodelCreator.createBoolLiteral(true));

        Boolean actualValue = provider.getBoolValue(variable);

        assertThat(actualValue).isTrue();
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
}
