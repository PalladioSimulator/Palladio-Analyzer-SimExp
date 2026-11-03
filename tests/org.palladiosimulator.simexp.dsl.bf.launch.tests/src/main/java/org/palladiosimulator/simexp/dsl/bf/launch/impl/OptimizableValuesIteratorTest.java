package org.palladiosimulator.simexp.dsl.bf.launch.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Literal;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

public class OptimizableValuesIteratorTest {
    private OptimizableValuesIterator iterator;

    private SmodelCreator smodelCreator;

    private Optimizable optimizable1;
    private Optimizable optimizable2;

    @Mock
    private IExpressionCalculator expressionCalculator;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        smodelCreator = new SmodelCreator();

        Literal literal1 = smodelCreator.createIntLiteral(1);
        Literal literal2 = smodelCreator.createIntLiteral(2);
        Literal literal3 = smodelCreator.createIntLiteral(3);
        when(expressionCalculator.calculateInteger(literal1)).thenReturn(1);
        when(expressionCalculator.calculateInteger(literal2)).thenReturn(2);
        when(expressionCalculator.calculateInteger(literal3)).thenReturn(3);

        SetBounds bounds1 = smodelCreator.createSetBounds(literal1);
        SetBounds bounds2 = smodelCreator.createSetBounds(literal2, literal3);
        optimizable1 = smodelCreator.createOptimizable("oa", DataType.INT, bounds1);
        optimizable2 = smodelCreator.createOptimizable("ob", DataType.INT, bounds2);
    }

    @Test
    public void testHasNextSingleFirst() {
        iterator = new OptimizableValuesIterator(Collections.singletonList(optimizable1), expressionCalculator);

        assertThat(iterator.hasNext()).isTrue();
    }

    @Test
    public void testHasNextSingleSecond() {
        iterator = new OptimizableValuesIterator(Collections.singletonList(optimizable1), expressionCalculator);
        iterator.next();

        assertThat(iterator.hasNext()).isFalse();
    }
}
