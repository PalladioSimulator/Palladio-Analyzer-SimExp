package org.palladiosimulator.simexp.dsl.ea.optimizer.smodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.ea.api.IEAFitnessEvaluator.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
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
        optimizableNormalizer = new OptimizableNormalizer();
        smodelCreator = new SmodelCreator();
    }

    @Test
    public void testToNormalizedIntSetPower0() {
        Literal literal = smodelCreator.createIntLiteral(1);
        SetBounds bounds = smodelCreator.createSetBounds(literal);
        Optimizable optimizable = smodelCreator.createOptimizable("intSetOptimizable", DataType.INT, bounds);

        SmodelBitChromosome actualChromosome = optimizableNormalizer.toNormalized(optimizable, calculator);

        assertEquals(0, actualChromosome.length());
    }

    @Test
    public void testToNormalizedIntSetPower1() {
        Literal literal1 = smodelCreator.createIntLiteral(1);
        Literal literal2 = smodelCreator.createIntLiteral(2);
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        Optimizable optimizable = smodelCreator.createOptimizable("intSetOptimizable", DataType.INT, bounds);

        SmodelBitChromosome actualChromosome = optimizableNormalizer.toNormalized(optimizable, calculator);

        assertEquals(1, actualChromosome.length());
    }

    @Test
    public void testToNormalizedIntSetPower2() {
        Literal literal1 = smodelCreator.createIntLiteral(1);
        Literal literal2 = smodelCreator.createIntLiteral(2);
        Literal literal3 = smodelCreator.createIntLiteral(3);
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2, literal3);
        Optimizable optimizable = smodelCreator.createOptimizable("intSetOptimizable", DataType.INT, bounds);

        SmodelBitChromosome actualChromosome = optimizableNormalizer.toNormalized(optimizable, calculator);

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

        SmodelBitChromosome actualChromosome = optimizableNormalizer.toNormalized(optimizable, calculator);

        assertEquals(1, actualChromosome.length());
    }

    @Test
    public void testToOptimizableInt0() {
        Literal literal1 = smodelCreator.createIntLiteral(1);
        Literal literal2 = smodelCreator.createIntLiteral(2);
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        Optimizable optimizable = smodelCreator.createOptimizable("optimizable", DataType.INT, bounds);
        SmodelBitset bitset = new SmodelBitset(1);
        SmodelBitChromosome actualChromosome = SmodelBitChromosome.of(bitset, optimizable);

        OptimizableValue<?> actualValue = optimizableNormalizer.toOptimizable(actualChromosome);

        assertEquals(0, actualValue.getValue());
        assertSame(actualValue.getOptimizable(), optimizable);
    }

    @Test
    public void testToOptimizableInt1() {
        Literal literal1 = smodelCreator.createIntLiteral(1);
        Literal literal2 = smodelCreator.createIntLiteral(2);
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        Optimizable optimizable = smodelCreator.createOptimizable("optimizable", DataType.INT, bounds);
        SmodelBitset bitset = new SmodelBitset(1);
        bitset.set(0);
        SmodelBitChromosome initialChromosome = SmodelBitChromosome.of(bitset, optimizable);

        OptimizableValue<?> actualValue = optimizableNormalizer.toOptimizable(initialChromosome);

        assertEquals(1, actualValue.getValue());
        assertSame(actualValue.getOptimizable(), optimizable);
    }
}
