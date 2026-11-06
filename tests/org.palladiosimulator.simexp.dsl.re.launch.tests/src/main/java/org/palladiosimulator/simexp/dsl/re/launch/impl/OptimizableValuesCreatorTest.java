package org.palladiosimulator.simexp.dsl.re.launch.impl;

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
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

public class OptimizableValuesCreatorTest {
    private OptimizableValuesCreator creator;

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
        SetBounds setBounds1 = smodelCreator.createSetBounds(setLiteral1);
        SetBounds setBounds2 = smodelCreator.createSetBounds(setLiteral2, setLiteral3);
        setOptimizable1 = smodelCreator.createOptimizable("oa", DataType.INT, setBounds1);
        setOptimizable2 = smodelCreator.createOptimizable("ob", DataType.INT, setBounds2);

        Literal rangeLiteralStart = smodelCreator.createIntLiteral(1);
        Literal rangeLiteralEnd = smodelCreator.createIntLiteral(2);
        Literal rangeLiteralStep = smodelCreator.createIntLiteral(1);
        when(expressionCalculator.calculateInteger(rangeLiteralStart)).thenReturn(1);
        when(expressionCalculator.calculateInteger(rangeLiteralEnd)).thenReturn(2);
        when(expressionCalculator.calculateInteger(rangeLiteralStep)).thenReturn(1);
        RangeBounds rangeBounds1 = smodelCreator.createRangeBoundsClosedOpen(rangeLiteralStart, rangeLiteralEnd,
                rangeLiteralStep);
        rangeOptimizable1 = smodelCreator.createOptimizable("oc", DataType.INT, rangeBounds1);

        creator = new OptimizableValuesCreator(expressionCalculator);
    }

    @Test
    public void testCombinationsSingleSet() {
        List<List<OptimizableValue<?>>> actualValues = creator.combinations(Collections.singletonList(setOptimizable1));

        List<List<OptimizableValue<?>>> expectedValues = new ArrayList<>();
        expectedValues.add(Collections.singletonList(new OptimizableValue<>(setOptimizable1, 1)));
        assertThat(actualValues).containsAll(expectedValues);
    }

    @Test
    public void testCombinationsSingleRange() {
        List<List<OptimizableValue<?>>> actualValues = creator
            .combinations(Collections.singletonList(rangeOptimizable1));

        List<List<OptimizableValue<?>>> expectedValues = new ArrayList<>();
        expectedValues.add(Collections.singletonList(new OptimizableValue<>(rangeOptimizable1, 1)));
        assertThat(actualValues).containsAll(expectedValues);
    }
}
