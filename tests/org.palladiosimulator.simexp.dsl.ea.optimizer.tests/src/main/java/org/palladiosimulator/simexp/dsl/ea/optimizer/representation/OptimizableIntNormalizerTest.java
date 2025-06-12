package org.palladiosimulator.simexp.dsl.ea.optimizer.representation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.smodel.api.IExpressionCalculator;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.dsl.smodel.smodel.BoolLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DoubleLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.IntLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Literal;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.StringLiteral;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

import io.jenetics.IntegerGene;
import io.jenetics.util.MSeq;

public class OptimizableIntNormalizerTest {
    private static final double DELTA = 1e-15;

    private OptimizableIntNormalizer optimizableNormalizer;

    @Mock
    private IExpressionCalculator calculator;
    @Mock
    private Optimizable boolOptimizable;
    @Mock
    private Optimizable intOptimizable;
    @Mock
    public Optimizable optimizable;

    private SmodelCreator smodelCreator;

    @Before
    public void setUp() {
        initMocks(this);
        when(calculator.getEpsilon()).thenReturn(DELTA);

        smodelCreator = new SmodelCreator();

        optimizableNormalizer = new OptimizableIntNormalizer(calculator);
    }

    @Test
    public void testToNormalizedBoolSet() {
        Literal literal1 = smodelCreator.createBoolLiteral(true);
        Literal literal2 = smodelCreator.createBoolLiteral(true);
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        Optimizable optimizable = smodelCreator.createOptimizable("boolSetOptimizable", DataType.BOOL, bounds);

        SmodelIntChromosome actualChromosome = optimizableNormalizer.toNormalized(optimizable);

        assertThat(actualChromosome.max()).isEqualTo(1);
    }

    @Test
    public void testToNormalizedIntSetPower0() {
        Literal literal = smodelCreator.createIntLiteral(1);
        SetBounds bounds = smodelCreator.createSetBounds(literal);
        Optimizable optimizable = smodelCreator.createOptimizable("intSetOptimizable", DataType.INT, bounds);

        SmodelIntChromosome actualChromosome = optimizableNormalizer.toNormalized(optimizable);

        assertEquals(1, actualChromosome.length());
        assertThat(actualChromosome.max()).isEqualTo(0);
    }

    @Test
    public void testToNormalizedIntSetPower1() {
        Literal literal1 = smodelCreator.createIntLiteral(1);
        Literal literal2 = smodelCreator.createIntLiteral(2);
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        Optimizable optimizable = smodelCreator.createOptimizable("intSetOptimizable", DataType.INT, bounds);

        SmodelIntChromosome actualChromosome = optimizableNormalizer.toNormalized(optimizable);

        assertEquals(1, actualChromosome.length());
        assertThat(actualChromosome.max()).isEqualTo(1);
    }

    @Test
    public void testToNormalizedDoubleSetPower1() {
        Literal literal1 = smodelCreator.createDoubleLiteral(1);
        Literal literal2 = smodelCreator.createDoubleLiteral(2);
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        Optimizable optimizable = smodelCreator.createOptimizable("doubleSetOptimizable", DataType.DOUBLE, bounds);

        SmodelIntChromosome actualChromosome = optimizableNormalizer.toNormalized(optimizable);

        assertEquals(1, actualChromosome.length());
        assertThat(actualChromosome.max()).isEqualTo(1);
    }

    @Test
    public void testToNormalizedIntSetPower2() {
        Literal literal1 = smodelCreator.createIntLiteral(1);
        Literal literal2 = smodelCreator.createIntLiteral(2);
        Literal literal3 = smodelCreator.createIntLiteral(3);
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2, literal3);
        Optimizable optimizable = smodelCreator.createOptimizable("intSetOptimizable", DataType.INT, bounds);

        SmodelIntChromosome actualChromosome = optimizableNormalizer.toNormalized(optimizable);

        assertEquals(1, actualChromosome.length());
        assertThat(actualChromosome.max()).isEqualTo(2);
    }

    @Test
    public void testToNormalizedStringSet() {
        Literal literal1 = smodelCreator.createStringLiteral("hello");
        Literal literal2 = smodelCreator.createStringLiteral("World");
        Literal literal3 = smodelCreator.createStringLiteral("!");
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2, literal3);
        Optimizable optimizable = smodelCreator.createOptimizable("stringSetOptimizable", DataType.STRING, bounds);

        SmodelIntChromosome actualChromosome = optimizableNormalizer.toNormalized(optimizable);

        assertEquals(1, actualChromosome.length());
        assertThat(actualChromosome.max()).isEqualTo(2);
    }

    @Test
    public void testToNormalizedIntRangePower1() {
        IntLiteral start = smodelCreator.createIntLiteral(1);
        IntLiteral end = smodelCreator.createIntLiteral(3);
        IntLiteral step = smodelCreator.createIntLiteral(1);
        when(calculator.calculateInteger(start)).thenReturn(start.getValue());
        when(calculator.calculateInteger(end)).thenReturn(end.getValue());
        when(calculator.calculateInteger(step)).thenReturn(step.getValue());
        RangeBounds bounds = smodelCreator.createRangeBoundsClosedOpen(start, end, step);
        Optimizable optimizable = smodelCreator.createOptimizable("intRangeOptimizable", DataType.INT, bounds);

        SmodelIntChromosome actualChromosome = optimizableNormalizer.toNormalized(optimizable);

        assertEquals(1, actualChromosome.length());
        assertThat(actualChromosome.max()).isEqualTo(1);
    }

    @Test
    public void testToNormalizedDoubleRangePower1() {
        DoubleLiteral start = smodelCreator.createDoubleLiteral(1);
        DoubleLiteral end = smodelCreator.createDoubleLiteral(3);
        DoubleLiteral step = smodelCreator.createDoubleLiteral(1);
        when(calculator.calculateDouble(start)).thenReturn(start.getValue());
        when(calculator.calculateDouble(end)).thenReturn(end.getValue());
        when(calculator.calculateDouble(step)).thenReturn(step.getValue());
        RangeBounds bounds = smodelCreator.createRangeBoundsOpenClosed(start, end, step);
        Optimizable optimizable = smodelCreator.createOptimizable("doubleRangeOptimizable", DataType.DOUBLE, bounds);

        SmodelIntChromosome actualChromosome = optimizableNormalizer.toNormalized(optimizable);

        assertEquals(1, actualChromosome.length());
        assertThat(actualChromosome.max()).isEqualTo(1);
    }

    @Test
    public void testToNormalizedDoubleWithSingleValueOptimizable() {
        DoubleLiteral literal1 = smodelCreator.createDoubleLiteral(1);
        DoubleLiteral literal2 = smodelCreator.createDoubleLiteral(2);
        when(calculator.calculateDouble(literal1)).thenReturn(literal1.getValue());
        when(calculator.calculateDouble(literal2)).thenReturn(literal2.getValue());
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        Optimizable twoValuesOptimizable = smodelCreator.createOptimizable("optimizable", DataType.DOUBLE, bounds);
        DoubleLiteral literal3 = smodelCreator.createDoubleLiteral(3);
        when(calculator.calculateDouble(literal3)).thenReturn(literal3.getValue());
        SetBounds singleValueBounds = smodelCreator.createSetBounds(literal3);
        Optimizable singleValueOptimizable = smodelCreator.createOptimizable("singleValueOptimizable", DataType.DOUBLE,
                singleValueBounds);
        List<Optimizable> optimizableList = List.of(twoValuesOptimizable, singleValueOptimizable);

        List<SmodelIntChromosome> actualChromosomes = optimizableNormalizer.toNormalized(optimizableList);

        assertEquals(2, actualChromosomes.size());
        assertEquals(twoValuesOptimizable, actualChromosomes.get(0)
            .getOptimizable());
    }

    @Test
    public void testToOptimizableBoolIndex0True() {
        BoolLiteral literal1 = smodelCreator.createBoolLiteral(true);
        BoolLiteral literal2 = smodelCreator.createBoolLiteral(false);
        when(calculator.calculateBoolean(literal1)).thenReturn(literal1.isTrue());
        when(calculator.calculateBoolean(literal2)).thenReturn(literal2.isTrue());
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        Optimizable optimizable = smodelCreator.createOptimizable("optimizable", DataType.BOOL, bounds);
        SmodelIntChromosome chromosome = createChromosome(optimizable, 1, 0);

        OptimizableValue<?> actualValue = optimizableNormalizer.toOptimizable(chromosome);

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
        SmodelIntChromosome chromosome = createChromosome(optimizable, 1, 1);

        OptimizableValue<?> actualValue = optimizableNormalizer.toOptimizable(chromosome);

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
        SmodelIntChromosome chromosome = createChromosome(optimizable, 1, 0);

        OptimizableValue<?> actualValue = optimizableNormalizer.toOptimizable(chromosome);

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
        SmodelIntChromosome chromosome = createChromosome(optimizable, 1, 1);

        OptimizableValue<?> actualValue = optimizableNormalizer.toOptimizable(chromosome);

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
        SmodelIntChromosome chromosome = createChromosome(optimizable, 1, 1);

        OptimizableValue<?> actualValue = optimizableNormalizer.toOptimizable(chromosome);

        assertEquals(2.0, actualValue.getValue());
        assertSame(actualValue.getOptimizable(), optimizable);
    }

    @Test
    public void testToOptimizableWithSingleValueOptimizable() {
        DoubleLiteral literal1 = smodelCreator.createDoubleLiteral(1);
        DoubleLiteral literal2 = smodelCreator.createDoubleLiteral(2);
        when(calculator.calculateDouble(literal1)).thenReturn(literal1.getValue());
        when(calculator.calculateDouble(literal2)).thenReturn(literal2.getValue());
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        Optimizable twoValuesOptimizable = smodelCreator.createOptimizable("optimizable", DataType.DOUBLE, bounds);
        DoubleLiteral literal3 = smodelCreator.createDoubleLiteral(3);
        when(calculator.calculateDouble(literal3)).thenReturn(literal3.getValue());
        SetBounds singleValueBounds = smodelCreator.createSetBounds(literal3);
        Optimizable singleValueOptimizable = smodelCreator.createOptimizable("singleValueOptimizable", DataType.DOUBLE,
                singleValueBounds);
        List<Optimizable> optimizableList = List.of(twoValuesOptimizable, singleValueOptimizable);
        List<SmodelIntChromosome> normalized = optimizableNormalizer.toNormalized(optimizableList);

        List<OptimizableValue<?>> optimizableValues = optimizableNormalizer.toOptimizableValues(normalized);

        assertEquals(2, optimizableValues.size());
        assertEquals(twoValuesOptimizable, optimizableValues.get(0)
            .getOptimizable());
        assertEquals(singleValueOptimizable, optimizableValues.get(1)
            .getOptimizable());
    }

    @Test
    public void testToOptimizableStringIndex1() {
        StringLiteral literal1 = smodelCreator.createStringLiteral("hello");
        StringLiteral literal2 = smodelCreator.createStringLiteral("World");
        when(calculator.calculateString(literal1)).thenReturn(literal1.getValue());
        when(calculator.calculateString(literal2)).thenReturn(literal2.getValue());
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        Optimizable optimizable = smodelCreator.createOptimizable("optimizable", DataType.STRING, bounds);
        SmodelIntChromosome chromosome = createChromosome(optimizable, 1, 1);

        OptimizableValue<?> actualValue = optimizableNormalizer.toOptimizable(chromosome);

        assertEquals("World", actualValue.getValue());
        assertSame(actualValue.getOptimizable(), optimizable);
    }

    private SmodelIntChromosome createChromosome(Optimizable optimizable, int max, int value) {
        SmodelIntChromosome chromosome = SmodelIntChromosome.of(optimizable, 0, max);
        MSeq<IntegerGene> genes = MSeq.of(chromosome);
        genes.set(0, chromosome.get(0)
            .newInstance(value));
        return chromosome.newInstance(genes.toISeq());
    }
}
