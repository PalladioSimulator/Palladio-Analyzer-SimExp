package org.palladiosimulator.simexp.dsl.bf.launch.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Literal;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

public class OptimizableValuesIteratorTest {
    private OptimizableValuesIterator iterator;

    private SmodelCreator smodelCreator;

    private Optimizable setOptimizable1;
    private Optimizable setOptimizable2;
    private Optimizable rangeOptimizable1;

    @Mock
    private IExpressionCalculator expressionCalculator;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        smodelCreator = new SmodelCreator();

        Literal setLiteral1 = smodelCreator.createIntLiteral(1);
        Literal setLiteral2 = smodelCreator.createIntLiteral(2);
        Literal setLiteral3 = smodelCreator.createIntLiteral(3);
        when(expressionCalculator.calculateInteger(setLiteral1)).thenReturn(1);
        when(expressionCalculator.calculateInteger(setLiteral2)).thenReturn(2);
        when(expressionCalculator.calculateInteger(setLiteral3)).thenReturn(3);

        SetBounds bounds1 = smodelCreator.createSetBounds(setLiteral1);
        SetBounds bounds2 = smodelCreator.createSetBounds(setLiteral2, setLiteral3);
        setOptimizable1 = smodelCreator.createOptimizable("oa", DataType.INT, bounds1);
        setOptimizable2 = smodelCreator.createOptimizable("ob", DataType.INT, bounds2);
    }

    @Test
    public void testHasNextSingleFirst() {
        iterator = new OptimizableValuesIterator(Collections.singletonList(setOptimizable1), expressionCalculator);

        assertThat(iterator.hasNext()).isTrue();
    }

    @Test
    public void testHasNextSingleSecond() {
        iterator = new OptimizableValuesIterator(Collections.singletonList(setOptimizable1), expressionCalculator);
        iterator.next();

        assertThat(iterator.hasNext()).isFalse();
    }

    @Test
    public void testNextSingleFirst() {
        iterator = new OptimizableValuesIterator(Collections.singletonList(setOptimizable1), expressionCalculator);

        List<OptimizableValue<?>> actualValues = iterator.next();

        List<OptimizableValue<?>> expectedValues = new ArrayList<>();
        expectedValues.add(new OptimizableValue<>(setOptimizable1, 1));
        assertThat(actualValues).containsAll(expectedValues);
    }
}
