package org.palladiosimulator.simexp.dsl.smodel.interpreter.value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

public class OptimizableValueProviderTest {
    private static final double DOUBLE_EPSILON = 1e-15;

    private OptimizableValueProvider valueProvider;
    private SmodelCreator smodelCreator;

    @Before
    public void setUp() throws Exception {
        valueProvider = new OptimizableValueProvider(Collections.emptyList());
        smodelCreator = new SmodelCreator();
    }

    @Test
    public void testGetBoolValue() {
        Optimizable optimizable = smodelCreator.createOptimizable("opt", DataType.BOOL, null);
        OptimizableValue<Boolean> optimizableValue = new OptimizableValue<>(optimizable, true);
        valueProvider = new OptimizableValueProvider(Collections.singletonList(optimizableValue));

        Boolean actualValue = valueProvider.getBoolValue(optimizable);

        assertThat(actualValue).isTrue();
    }

    @Test
    public void testGetStringValue() {
        Optimizable optimizable = smodelCreator.createOptimizable("opt", DataType.STRING, null);
        OptimizableValue<String> optimizableValue = new OptimizableValue<>(optimizable, "a");
        valueProvider = new OptimizableValueProvider(Collections.singletonList(optimizableValue));

        String actualValue = valueProvider.getStringValue(optimizable);

        assertThat(actualValue).isEqualTo("a");
    }

    @Test
    public void testGetIntegerValue() throws Exception {
        Optimizable optimizable = smodelCreator.createOptimizable("opt", DataType.INT, null);
        OptimizableValue<Integer> optimizableValue = new OptimizableValue<>(optimizable, 1);
        valueProvider = new OptimizableValueProvider(Collections.singletonList(optimizableValue));

        Integer actualValue = valueProvider.getIntegerValue(optimizable);

        assertThat(actualValue).isEqualTo(1);
    }

    @Test
    public void testGetDoubleValue() throws Exception {
        Optimizable optimizable = smodelCreator.createOptimizable("opt", DataType.DOUBLE, null);
        OptimizableValue<Double> optimizableValue = new OptimizableValue<>(optimizable, 1.0);
        valueProvider = new OptimizableValueProvider(Collections.singletonList(optimizableValue));

        Double actualValue = valueProvider.getDoubleValue(optimizable);

        assertThat(actualValue).isEqualTo(1.0, offset(DOUBLE_EPSILON));
    }
}
