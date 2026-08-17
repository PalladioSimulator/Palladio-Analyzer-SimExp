package org.palladiosimulator.simexp.dsl.smodel.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Literal;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

public class OptimizableValueTest {
    private SmodelCreator smodelCreator;
    private Optimizable optimizable1;
    private SetBounds bounds;

    @Before
    public void setUp() throws Exception {
        smodelCreator = new SmodelCreator();

        Literal literal1 = smodelCreator.createIntLiteral(1);
        Literal literal2 = smodelCreator.createIntLiteral(2);
        bounds = smodelCreator.createSetBounds(literal1, literal2);
        optimizable1 = smodelCreator.createOptimizable("o1", DataType.INT, bounds);
    }

    @Test
    public void testEqualTrue() {
        OptimizableValue<Integer> optimizableValue1 = new OptimizableValue<>(optimizable1, 1);
        Optimizable optimizable2 = smodelCreator.createOptimizable("o1", DataType.INT, bounds);
        OptimizableValue<Integer> optimizableValue2 = new OptimizableValue<>(optimizable2, 1);

        boolean actualEquals = optimizableValue1.equals(optimizableValue2);

        assertThat(actualEquals).isTrue();
    }

    @Test
    public void testEqualValue() {
        OptimizableValue<Integer> optimizableValue1 = new OptimizableValue<>(optimizable1, 1);
        Optimizable optimizable2 = smodelCreator.createOptimizable("o1", DataType.INT, bounds);
        OptimizableValue<Integer> optimizableValue2 = new OptimizableValue<>(optimizable2, 2);

        boolean actualEquals = optimizableValue1.equals(optimizableValue2);

        assertThat(actualEquals).isFalse();
    }

    @Test
    public void testEqualName() {
        OptimizableValue<Integer> optimizableValue1 = new OptimizableValue<>(optimizable1, 1);
        Optimizable optimizable2 = smodelCreator.createOptimizable("o2", DataType.INT, bounds);
        OptimizableValue<Integer> optimizableValue2 = new OptimizableValue<>(optimizable2, 1);

        boolean actualEquals = optimizableValue1.equals(optimizableValue2);

        assertThat(actualEquals).isFalse();
    }

    @Test
    public void testEqualDataType() {
        OptimizableValue<Integer> optimizableValue1 = new OptimizableValue<>(optimizable1, 1);
        Optimizable optimizable2 = smodelCreator.createOptimizable("o1", DataType.DOUBLE, bounds);
        OptimizableValue<Integer> optimizableValue2 = new OptimizableValue<>(optimizable2, 1);

        boolean actualEquals = optimizableValue1.equals(optimizableValue2);

        assertThat(actualEquals).isFalse();
    }
}
