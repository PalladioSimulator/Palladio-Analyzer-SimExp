package org.palladiosimulator.simexp.pcm.modelled.config.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

public class SimpleOptimizedConfigurationTest {
    private static final double DOUBLE_EPSILON = 1e-15;

    private SimpleOptimizedConfiguration configuration;

    private SmodelCreator smodelCreator;

    @Before
    public void setUp() throws Exception {
        smodelCreator = new SmodelCreator();

        configuration = new SimpleOptimizedConfiguration(Collections.emptyList());
    }

    @Test
    public void testGetOptimizableValueSetBool() {
        SetBounds bounds = smodelCreator.createSetBounds(smodelCreator.createBoolLiteral(true));
        Optimizable optimizable = smodelCreator.createOptimizable("opt", DataType.BOOL, bounds);

        OptimizableValue<?> actualOptimizableValue = configuration.getOptimizableValue(optimizable);

        assertThat((Boolean) actualOptimizableValue.getValue()).isTrue();
    }

    @Test
    public void testGetOptimizableValueSetInt() {
        SetBounds bounds = smodelCreator.createSetBounds(smodelCreator.createIntLiteral(1));
        Optimizable optimizable = smodelCreator.createOptimizable("opt", DataType.INT, bounds);

        OptimizableValue<?> actualOptimizableValue = configuration.getOptimizableValue(optimizable);

        assertThat((Integer) actualOptimizableValue.getValue()).isEqualTo(1);
    }

    @Test
    public void testGetOptimizableValueSetDouble() {
        SetBounds bounds = smodelCreator.createSetBounds(smodelCreator.createDoubleLiteral(1.0));
        Optimizable optimizable = smodelCreator.createOptimizable("opt", DataType.DOUBLE, bounds);

        OptimizableValue<?> actualOptimizableValue = configuration.getOptimizableValue(optimizable);

        assertThat((Double) actualOptimizableValue.getValue()).isEqualTo(1.0, offset(DOUBLE_EPSILON));
    }

    @Test
    public void testGetOptimizableValueSetString() {
        SetBounds bounds = smodelCreator.createSetBounds(smodelCreator.createStringLiteral("a"));
        Optimizable optimizable = smodelCreator.createOptimizable("opt", DataType.STRING, bounds);

        OptimizableValue<?> actualOptimizableValue = configuration.getOptimizableValue(optimizable);

        assertThat((String) actualOptimizableValue.getValue()).isEqualTo("a");
    }

    @Test
    public void testGetOptimizableValueRangeInt() {
        RangeBounds bounds = smodelCreator.createRangeBoundsClosedOpen(smodelCreator.createIntLiteral(1),
                smodelCreator.createIntLiteral(2), smodelCreator.createIntLiteral(1));
        Optimizable optimizable = smodelCreator.createOptimizable("opt", DataType.INT, bounds);

        OptimizableValue<?> actualOptimizableValue = configuration.getOptimizableValue(optimizable);

        assertThat((Integer) actualOptimizableValue.getValue()).isEqualTo(1);
    }

    @Test
    public void testGetOptimizableValueRangeDouble() {
        RangeBounds bounds = smodelCreator.createRangeBoundsClosedOpen(smodelCreator.createDoubleLiteral(1.0),
                smodelCreator.createDoubleLiteral(2.0), smodelCreator.createDoubleLiteral(1.0));
        Optimizable optimizable = smodelCreator.createOptimizable("opt", DataType.DOUBLE, bounds);

        OptimizableValue<?> actualOptimizableValue = configuration.getOptimizableValue(optimizable);

        assertThat((Double) actualOptimizableValue.getValue()).isEqualTo(1.0, offset(DOUBLE_EPSILON));
    }
}
