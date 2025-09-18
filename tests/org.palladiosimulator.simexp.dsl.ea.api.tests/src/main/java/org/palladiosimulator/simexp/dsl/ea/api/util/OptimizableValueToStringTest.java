package org.palladiosimulator.simexp.dsl.ea.api.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Literal;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

public class OptimizableValueToStringTest {
    private OptimizableValueToString optimizableValueToString;

    private SmodelCreator smodelCreator;
    private Optimizable optimizable1;
    private Optimizable optimizable2;
    private OptimizableValue<Integer> optimizableValue1;
    private OptimizableValue<Integer> optimizableValue2;

    @Before
    public void setUp() throws Exception {
        smodelCreator = new SmodelCreator();

        Literal literal1 = smodelCreator.createIntLiteral(1);
        Literal literal2 = smodelCreator.createIntLiteral(2);
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        optimizable1 = smodelCreator.createOptimizable("oa", DataType.INT, bounds);
        optimizable2 = smodelCreator.createOptimizable("ob", DataType.INT, bounds);
        optimizableValue1 = new OptimizableValue<>(optimizable1, 1);
        optimizableValue2 = new OptimizableValue<>(optimizable2, 1);

        optimizableValueToString = new OptimizableValueToString();
    }

    @Test
    public void testAsStringSorted() {
        List<OptimizableValue<?>> optimizableValues = new ArrayList<>();
        optimizableValues.add(optimizableValue1);
        optimizableValues.add(optimizableValue2);

        String actualString = optimizableValueToString.asString(optimizableValues);

        assertThat(actualString).isEqualTo("oa: 1,ob: 1");
    }

    @Test
    public void testAsStringUnsorted() {
        List<OptimizableValue<?>> optimizableValues = new ArrayList<>();
        optimizableValues.add(optimizableValue2);
        optimizableValues.add(optimizableValue1);

        String actualString = optimizableValueToString.asString(optimizableValues);

        assertThat(actualString).isEqualTo("oa: 1,ob: 1");
    }
}
