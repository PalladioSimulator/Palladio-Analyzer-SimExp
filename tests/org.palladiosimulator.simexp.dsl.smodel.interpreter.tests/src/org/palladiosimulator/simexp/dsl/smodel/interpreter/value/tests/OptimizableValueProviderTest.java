package org.palladiosimulator.simexp.dsl.smodel.interpreter.value.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.simexp.dsl.smodel.interpreter.value.OptimizableValueProvider;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DoubleLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.EnvVariable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelFactory;

public class OptimizableValueProviderTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testGetBoolValueNotSupported() {
        OptimizableValueProvider valueProvider = new OptimizableValueProvider();
        Optimizable expectedField = SmodelFactory.eINSTANCE.createOptimizable();
        expectedField.setDataType(DataType.BOOL);

        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> {
            valueProvider.getBoolValue(expectedField);
        })
            .withMessage("Boolean Field not supported");
    }

    @Test
    public void testGetStringValueNotSupported() {
        OptimizableValueProvider valueProvider = new OptimizableValueProvider();
        Optimizable expectedField = SmodelFactory.eINSTANCE.createOptimizable();
        expectedField.setDataType(DataType.STRING);

        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> {
            valueProvider.getStringValue(expectedField);
        })
            .withMessage("String Field not supported");
    }

    @Test
    public void testGetIntegerValueForUnsupportedField() throws Exception {
        OptimizableValueProvider valueProvider = new OptimizableValueProvider();
        EnvVariable expectedField = SmodelFactory.eINSTANCE.createEnvVariable();

        Integer actualValue = valueProvider.getIntegerValue(expectedField);

        assertThat(actualValue).isNull();

    }

    @Test
    public void testGetIntegerValueForSet() throws Exception {
        OptimizableValueProvider valueProvider = new OptimizableValueProvider();
        Optimizable expectedField = SmodelFactory.eINSTANCE.createOptimizable();
        expectedField.setDataType(DataType.INT);
        SetBounds bounds = SmodelFactory.eINSTANCE.createSetBounds();
        IntLiteral expectedValue1 = createIntLiteral(2);
        IntLiteral expectedValue2 = createIntLiteral(1);
        bounds.getValues()
            .add(expectedValue1);
        bounds.getValues()
            .add(expectedValue2);
        expectedField.setValues(bounds);

        Integer actualValue = valueProvider.getIntegerValue(expectedField);

        assertThat(actualValue).isEqualTo(expectedValue1.getValue());
    }

    @Test
    public void testGetIntegerValueForRange() throws Exception {
        OptimizableValueProvider valueProvider = new OptimizableValueProvider();
        Optimizable expectedField = SmodelFactory.eINSTANCE.createOptimizable();
        expectedField.setDataType(DataType.INT);
        RangeBounds bounds = SmodelFactory.eINSTANCE.createRangeBounds();
        IntLiteral expectedValue1 = createIntLiteral(2);
        IntLiteral expectedValue2 = createIntLiteral(1);
        bounds.setStartValue(expectedValue1);
        bounds.setEndValue(expectedValue2);
        expectedField.setValues(bounds);

        Integer actualValue = valueProvider.getIntegerValue(expectedField);

        assertThat(actualValue).isEqualTo(expectedValue1.getValue());
    }

    @Test
    public void testGetDoubleValueForUnsupportedField() throws Exception {
        OptimizableValueProvider valueProvider = new OptimizableValueProvider();
        EnvVariable expectedField = SmodelFactory.eINSTANCE.createEnvVariable();

        Double actualValue = valueProvider.getDoubleValue(expectedField);

        assertThat(actualValue).isNull();

    }

    @Test
    public void testGetDoubleValueForSet() throws Exception {
        OptimizableValueProvider valueProvider = new OptimizableValueProvider();
        Optimizable expectedField = SmodelFactory.eINSTANCE.createOptimizable();
        expectedField.setDataType(DataType.DOUBLE);
        SetBounds bounds = SmodelFactory.eINSTANCE.createSetBounds();
        DoubleLiteral expectedValue1 = createDoubleLiteral(2.0);
        DoubleLiteral expectedValue2 = createDoubleLiteral(1.0);
        bounds.getValues()
            .add(expectedValue1);
        bounds.getValues()
            .add(expectedValue2);
        expectedField.setValues(bounds);

        Double actualValue = valueProvider.getDoubleValue(expectedField);

        assertThat(actualValue).isEqualTo(expectedValue1.getValue());
    }

    @Test
    public void testGetDoubleValueForRange() throws Exception {
        OptimizableValueProvider valueProvider = new OptimizableValueProvider();
        Optimizable expectedField = SmodelFactory.eINSTANCE.createOptimizable();
        expectedField.setDataType(DataType.DOUBLE);
        RangeBounds bounds = SmodelFactory.eINSTANCE.createRangeBounds();
        DoubleLiteral expectedValue1 = createDoubleLiteral(2.0);
        DoubleLiteral expectedValue2 = createDoubleLiteral(1.0);
        bounds.setStartValue(expectedValue1);
        bounds.setEndValue(expectedValue2);
        expectedField.setValues(bounds);

        Double actualValue = valueProvider.getDoubleValue(expectedField);

        assertThat(actualValue).isEqualTo(expectedValue1.getValue());
    }

    private IntLiteral createIntLiteral(int value) {
        IntLiteral literal = SmodelFactory.eINSTANCE.createIntLiteral();
        literal.setValue(value);
        return literal;
    }

    private DoubleLiteral createDoubleLiteral(double value) {
        DoubleLiteral literal = SmodelFactory.eINSTANCE.createDoubleLiteral();
        literal.setValue(value);
        return literal;
    }

}
