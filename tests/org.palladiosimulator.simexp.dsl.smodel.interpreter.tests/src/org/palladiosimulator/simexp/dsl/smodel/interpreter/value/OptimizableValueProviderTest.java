package org.palladiosimulator.simexp.dsl.smodel.interpreter.value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DoubleLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.EnvVariable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelFactory;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

public class OptimizableValueProviderTest {

    private OptimizableValueProvider valueProvider;
    private SmodelCreator smodelCreator;

    @Before
    public void setUp() throws Exception {
        valueProvider = new OptimizableValueProvider();
        smodelCreator = new SmodelCreator();
    }

    @Test
    public void testGetBoolValueNotSupported() {
        Optimizable expectedField = smodelCreator.createOptimizable("boolOpt", DataType.BOOL, null);

        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> {
            valueProvider.getBoolValue(expectedField);
        })
            .withMessage("Boolean Field not supported");
    }

    @Test
    public void testGetStringValueNotSupported() {
        Optimizable expectedField = smodelCreator.createOptimizable("stringOpt", DataType.STRING, null);

        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> {
            valueProvider.getStringValue(expectedField);
        })
            .withMessage("String Field not supported");
    }

    @Test
    public void testGetIntegerValueForUnsupportedField() throws Exception {
        EnvVariable expectedField = SmodelFactory.eINSTANCE.createEnvVariable();

        Integer actualValue = valueProvider.getIntegerValue(expectedField);

        assertThat(actualValue).isNull();

    }

    @Test
    public void testGetIntegerValueForSet() throws Exception {
        IntLiteral expectedValue1 = smodelCreator.createIntLiteral(2);
        IntLiteral expectedValue2 = smodelCreator.createIntLiteral(1);
        SetBounds bounds = smodelCreator.createSetBounds(expectedValue1, expectedValue2);
        Optimizable expectedField = smodelCreator.createOptimizable("IntOpt", DataType.INT, bounds);

        Integer actualValue = valueProvider.getIntegerValue(expectedField);

        assertThat(actualValue).isEqualTo(expectedValue1.getValue());
    }

    @Test
    public void testGetIntegerValueForRange() throws Exception {
        IntLiteral expectedValue1 = smodelCreator.createIntLiteral(2);
        IntLiteral expectedValue2 = smodelCreator.createIntLiteral(1);
        RangeBounds bounds = smodelCreator.createRangeBounds(expectedValue1, expectedValue2, null);
        Optimizable expectedField = smodelCreator.createOptimizable("IntOpt", DataType.INT, bounds);

        Integer actualValue = valueProvider.getIntegerValue(expectedField);

        assertThat(actualValue).isEqualTo(expectedValue1.getValue());
    }

    @Test
    public void testGetDoubleValueForUnsupportedField() throws Exception {
        EnvVariable expectedField = SmodelFactory.eINSTANCE.createEnvVariable();

        Double actualValue = valueProvider.getDoubleValue(expectedField);

        assertThat(actualValue).isNull();

    }

    @Test
    public void testGetDoubleValueForSet() throws Exception {
        DoubleLiteral expectedValue1 = smodelCreator.createDoubleLiteral(2.0);
        DoubleLiteral expectedValue2 = smodelCreator.createDoubleLiteral(1.0);
        SetBounds bounds = smodelCreator.createSetBounds(expectedValue1, expectedValue2);
        Optimizable expectedField = smodelCreator.createOptimizable("DoubleOpt", DataType.DOUBLE, bounds);

        Double actualValue = valueProvider.getDoubleValue(expectedField);

        assertThat(actualValue).isEqualTo(expectedValue1.getValue());
    }

    @Test
    public void testGetDoubleValueForRange() throws Exception {
        DoubleLiteral expectedValue1 = smodelCreator.createDoubleLiteral(2.0);
        DoubleLiteral expectedValue2 = smodelCreator.createDoubleLiteral(1.0);
        RangeBounds bounds = smodelCreator.createRangeBounds(expectedValue1, expectedValue2, null);
        Optimizable expectedField = smodelCreator.createOptimizable("DoubleOpt", DataType.DOUBLE, bounds);

        Double actualValue = valueProvider.getDoubleValue(expectedField);

        assertThat(actualValue).isEqualTo(expectedValue1.getValue());
    }

}
