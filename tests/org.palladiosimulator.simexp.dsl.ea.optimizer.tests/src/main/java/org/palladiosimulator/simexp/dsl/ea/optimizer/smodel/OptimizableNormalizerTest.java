package org.palladiosimulator.simexp.dsl.ea.optimizer.smodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator.OptimizableValue;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelBitset;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DoubleLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Literal;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

public class OptimizableNormalizerTest {

    private OptimizableNormalizer optimizableNormalizer;
    private SmodelCreator smodelCreator;

    @Mock
    private IExpressionCalculator calculator;

    @Before
    public void setUp() {
        initMocks(this);
        optimizableNormalizer = new OptimizableNormalizer(calculator);
        smodelCreator = new SmodelCreator();
    }

    @Test
    public void testToNormalizedBoolSet() {
        Literal literal1 = smodelCreator.createBoolLiteral(true);
        Literal literal2 = smodelCreator.createBoolLiteral(true);
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        Optimizable optimizable = smodelCreator.createOptimizable("boolSetOptimizable", DataType.BOOL, bounds);

        SmodelBitChromosome actualChromosome = optimizableNormalizer.toNormalized(optimizable);

        assertEquals(1, actualChromosome.length());
    }

    @Test
    public void testToNormalizedIntSetPower0() {
        Literal literal = smodelCreator.createIntLiteral(1);
        SetBounds bounds = smodelCreator.createSetBounds(literal);
        Optimizable optimizable = smodelCreator.createOptimizable("intSetOptimizable", DataType.INT, bounds);

        SmodelBitChromosome actualChromosome = optimizableNormalizer.toNormalized(optimizable);

        assertEquals(0, actualChromosome.length());
    }

    @Test
    public void testToNormalizedIntSetPower1() {
        Literal literal1 = smodelCreator.createIntLiteral(1);
        Literal literal2 = smodelCreator.createIntLiteral(2);
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        Optimizable optimizable = smodelCreator.createOptimizable("intSetOptimizable", DataType.INT, bounds);

        SmodelBitChromosome actualChromosome = optimizableNormalizer.toNormalized(optimizable);

        assertEquals(1, actualChromosome.length());
    }

    @Test
    public void testToNormalizedDoubleSetPower1() {
        Literal literal1 = smodelCreator.createDoubleLiteral(1);
        Literal literal2 = smodelCreator.createDoubleLiteral(2);
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        Optimizable optimizable = smodelCreator.createOptimizable("doubleSetOptimizable", DataType.DOUBLE, bounds);

        SmodelBitChromosome actualChromosome = optimizableNormalizer.toNormalized(optimizable);

        assertEquals(1, actualChromosome.length());
    }

    @Test
    public void testToNormalizedIntSetPower2() {
        Literal literal1 = smodelCreator.createIntLiteral(1);
        Literal literal2 = smodelCreator.createIntLiteral(2);
        Literal literal3 = smodelCreator.createIntLiteral(3);
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2, literal3);
        Optimizable optimizable = smodelCreator.createOptimizable("intSetOptimizable", DataType.INT, bounds);

        SmodelBitChromosome actualChromosome = optimizableNormalizer.toNormalized(optimizable);

        assertEquals(2, actualChromosome.length());
    }

    @Test
    public void testToNormalizedIntRangePower1() {
        IntLiteral start = smodelCreator.createIntLiteral(1);
        IntLiteral end = smodelCreator.createIntLiteral(3);
        IntLiteral step = smodelCreator.createIntLiteral(1);
        when(calculator.calculateInteger(start)).thenReturn(start.getValue());
        when(calculator.calculateInteger(end)).thenReturn(end.getValue());
        when(calculator.calculateInteger(step)).thenReturn(step.getValue());
        RangeBounds bounds = smodelCreator.createRangeBounds(start, end, step);
        Optimizable optimizable = smodelCreator.createOptimizable("intRangeOptimizable", DataType.INT, bounds);

        SmodelBitChromosome actualChromosome = optimizableNormalizer.toNormalized(optimizable);

        assertEquals(1, actualChromosome.length());
    }

    @Test
    public void testToNormalizedDoubleRangePower1() {
        DoubleLiteral start = smodelCreator.createDoubleLiteral(1);
        DoubleLiteral end = smodelCreator.createDoubleLiteral(3);
        DoubleLiteral step = smodelCreator.createDoubleLiteral(1);
        when(calculator.calculateDouble(start)).thenReturn(start.getValue());
        when(calculator.calculateDouble(end)).thenReturn(end.getValue());
        when(calculator.calculateDouble(step)).thenReturn(step.getValue());
        RangeBounds bounds = smodelCreator.createRangeBounds(start, end, step);
        Optimizable optimizable = smodelCreator.createOptimizable("doubleRangeOptimizable", DataType.DOUBLE, bounds);

        SmodelBitChromosome actualChromosome = optimizableNormalizer.toNormalized(optimizable);

        assertEquals(1, actualChromosome.length());
    }

    @Test
    public void testToOptimizableBoolIndex0True() {
        BoolLiteral literal1 = smodelCreator.createBoolLiteral(true);
        BoolLiteral literal2 = smodelCreator.createBoolLiteral(false);
        when(calculator.calculateBoolean(literal1)).thenReturn(literal1.isTrue());
        when(calculator.calculateBoolean(literal2)).thenReturn(literal2.isTrue());
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        Optimizable optimizable = smodelCreator.createOptimizable("optimizable", DataType.BOOL, bounds);
        SmodelBitset bitset = new SmodelBitset(1);
        SmodelBitChromosome actualChromosome = SmodelBitChromosome.of(bitset, optimizable, 2);

        OptimizableValue<?> actualValue = optimizableNormalizer.toOptimizable(actualChromosome);

        assertEquals(true, actualValue.getValue());
        assertSame(actualValue.getOptimizable(), optimizable);
    }

    @Test
    public void testToOptimizableBoolIndex0False() {
        BoolLiteral literal1 = smodelCreator.createBoolLiteral(true);
        BoolLiteral literal2 = smodelCreator.createBoolLiteral(false);
        when(calculator.calculateBoolean(literal1)).thenReturn(literal1.isTrue());
        when(calculator.calculateBoolean(literal2)).thenReturn(literal2.isTrue());
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        Optimizable optimizable = smodelCreator.createOptimizable("optimizable", DataType.BOOL, bounds);
        SmodelBitset bitset = new SmodelBitset(1);
        bitset.set(0);
        SmodelBitChromosome actualChromosome = SmodelBitChromosome.of(bitset, optimizable, 2);

        OptimizableValue<?> actualValue = optimizableNormalizer.toOptimizable(actualChromosome);

        assertEquals(false, actualValue.getValue());
        assertSame(actualValue.getOptimizable(), optimizable);
    }

    @Test
    public void testToOptimizableIntIndex0() {
        IntLiteral literal1 = smodelCreator.createIntLiteral(1);
        IntLiteral literal2 = smodelCreator.createIntLiteral(2);
        when(calculator.calculateInteger(literal1)).thenReturn(literal1.getValue());
        when(calculator.calculateInteger(literal2)).thenReturn(literal2.getValue());
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        Optimizable optimizable = smodelCreator.createOptimizable("optimizable", DataType.INT, bounds);
        SmodelBitset bitset = new SmodelBitset(1);
        SmodelBitChromosome actualChromosome = SmodelBitChromosome.of(bitset, optimizable, 2);

        OptimizableValue<?> actualValue = optimizableNormalizer.toOptimizable(actualChromosome);

        assertEquals(1, actualValue.getValue());
        assertSame(actualValue.getOptimizable(), optimizable);
    }

    @Test
    public void testToOptimizableIntIndex1() {
        IntLiteral literal1 = smodelCreator.createIntLiteral(1);
        IntLiteral literal2 = smodelCreator.createIntLiteral(2);
        when(calculator.calculateInteger(literal1)).thenReturn(literal1.getValue());
        when(calculator.calculateInteger(literal2)).thenReturn(literal2.getValue());
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        Optimizable optimizable = smodelCreator.createOptimizable("optimizable", DataType.INT, bounds);
        SmodelBitset bitset = new SmodelBitset(1);
        bitset.set(0);
        SmodelBitChromosome initialChromosome = SmodelBitChromosome.of(bitset, optimizable, 2);

        OptimizableValue<?> actualValue = optimizableNormalizer.toOptimizable(initialChromosome);

        assertEquals(2, actualValue.getValue());
        assertSame(actualValue.getOptimizable(), optimizable);
    }

    @Test
    public void testToOptimizableDoubleIndex1() {
        DoubleLiteral literal1 = smodelCreator.createDoubleLiteral(1);
        DoubleLiteral literal2 = smodelCreator.createDoubleLiteral(2);
        when(calculator.calculateDouble(literal1)).thenReturn(literal1.getValue());
        when(calculator.calculateDouble(literal2)).thenReturn(literal2.getValue());
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        Optimizable optimizable = smodelCreator.createOptimizable("optimizable", DataType.DOUBLE, bounds);
        SmodelBitset bitset = new SmodelBitset(1);
        bitset.set(0);
        SmodelBitChromosome initialChromosome = SmodelBitChromosome.of(bitset, optimizable, 2);

        OptimizableValue<?> actualValue = optimizableNormalizer.toOptimizable(initialChromosome);

        assertEquals(2.0, actualValue.getValue());
        assertSame(actualValue.getOptimizable(), optimizable);
    }

    // TODO nbruening: Tests for isValid() and newInstance() method
}
