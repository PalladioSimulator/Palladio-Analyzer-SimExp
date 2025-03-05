package org.palladiosimulator.simexp.dsl.ea.optimizer.smodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.palladiosimulator.simexp.dsl.ea.optimizer.representation.SmodelIntegerChromosome;
import org.palladiosimulator.simexp.dsl.ea.optimizer.utility.SetBoundsHelper;
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
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

import io.jenetics.util.IntRange;
import io.jenetics.util.RandomRegistry;

public class OptimizableIntegerChromoNormalizerTest {

    private final List<Integer> smallIntList = List.of(1, 3, 7, 3, 8, 2, 9);

    private OptimizableIntegerChromoNormalizer optimizableNormalizer;

    private SmodelCreator smodelCreator;

    private SetBoundsHelper setBoundsHelper;

    @Mock
    private IExpressionCalculator calculator;

    @Mock
    private Optimizable boolOptimizable;

    @Mock
    private Optimizable intOptimizable;

    @Mock
    public Optimizable optimizable;

    @Before
    public void setUp() {
        initMocks(this);
        optimizableNormalizer = new OptimizableIntegerChromoNormalizer(calculator);
        setBoundsHelper = new SetBoundsHelper();
        smodelCreator = new SmodelCreator();
    }

    @Test
    public void testToNormalizedBoolSet() {
        Literal literal1 = smodelCreator.createBoolLiteral(true);
        Literal literal2 = smodelCreator.createBoolLiteral(true);
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        Optimizable optimizable = smodelCreator.createOptimizable("boolSetOptimizable", DataType.BOOL, bounds);

        SmodelIntegerChromosome actualChromosome = optimizableNormalizer.toNormalized(optimizable);

        assertEquals(1, actualChromosome.length());
    }

    @Test
    public void testToNormalizedIntSetPower0() {
        Literal literal = smodelCreator.createIntLiteral(1);
        SetBounds bounds = smodelCreator.createSetBounds(literal);
        Optimizable optimizable = smodelCreator.createOptimizable("intSetOptimizable", DataType.INT, bounds);

        SmodelIntegerChromosome actualChromosome = optimizableNormalizer.toNormalized(optimizable);

        assertEquals(1, actualChromosome.length());
    }

    @Test
    public void testToNormalizedIntSetPower1() {
        Literal literal1 = smodelCreator.createIntLiteral(1);
        Literal literal2 = smodelCreator.createIntLiteral(2);
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        Optimizable optimizable = smodelCreator.createOptimizable("intSetOptimizable", DataType.INT, bounds);

        SmodelIntegerChromosome actualChromosome = optimizableNormalizer.toNormalized(optimizable);

        assertEquals(1, actualChromosome.length());
    }

    @Test
    public void testToNormalizedDoubleSetPower1() {
        Literal literal1 = smodelCreator.createDoubleLiteral(1);
        Literal literal2 = smodelCreator.createDoubleLiteral(2);
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        Optimizable optimizable = smodelCreator.createOptimizable("doubleSetOptimizable", DataType.DOUBLE, bounds);

        Function<? super Random, SmodelIntegerChromosome> func = (Random r) -> {
            return optimizableNormalizer.toNormalized(optimizable);
        };
        SmodelIntegerChromosome actualChromosome = RandomRegistry.with(new Random(42), func);

        assertEquals(1, actualChromosome.length());
        assertEquals(1, actualChromosome.intValue());
    }

    @Test
    public void testToNormalizedIntSetPower2() {
        Literal literal1 = smodelCreator.createIntLiteral(1);
        Literal literal2 = smodelCreator.createIntLiteral(2);
        Literal literal3 = smodelCreator.createIntLiteral(3);
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2, literal3);
        Optimizable optimizable = smodelCreator.createOptimizable("intSetOptimizable", DataType.INT, bounds);

        Function<? super Random, SmodelIntegerChromosome> func = (Random r) -> {
            return optimizableNormalizer.toNormalized(optimizable);
        };
        SmodelIntegerChromosome actualChromosome = RandomRegistry.with(new Random(42), func);

        assertEquals(1, actualChromosome.length());
        assertEquals(2, actualChromosome.intValue());
    }

    @Test
    public void testToNormalizedStringSet() {
        Literal literal1 = smodelCreator.createStringLiteral("hello");
        Literal literal2 = smodelCreator.createStringLiteral("World");
        Literal literal3 = smodelCreator.createStringLiteral("!");
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2, literal3);
        Optimizable optimizable = smodelCreator.createOptimizable("stringSetOptimizable", DataType.STRING, bounds);

        Function<? super Random, SmodelIntegerChromosome> func = (Random r) -> {
            return optimizableNormalizer.toNormalized(optimizable);
        };
        SmodelIntegerChromosome actualChromosome = RandomRegistry.with(new Random(42), func);

        assertEquals(1, actualChromosome.length());
        assertEquals(2, actualChromosome.intValue());
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

        Function<? super Random, SmodelIntegerChromosome> func = (Random r) -> {
            return optimizableNormalizer.toNormalized(optimizable);
        };
        SmodelIntegerChromosome actualChromosome = RandomRegistry.with(new Random(42), func);

        assertEquals(1, actualChromosome.length());
        assertEquals(1, actualChromosome.intValue());
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

        Function<? super Random, SmodelIntegerChromosome> func = (Random r) -> {
            return optimizableNormalizer.toNormalized(optimizable);
        };
        SmodelIntegerChromosome actualChromosome = RandomRegistry.with(new Random(42), func);

        assertEquals(1, actualChromosome.length());
        assertEquals(1, actualChromosome.intValue());
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

        List<SmodelIntegerChromosome> normalized = optimizableNormalizer.toNormalized(optimizableList);

        assertEquals(2, normalized.size());
        assertEquals(twoValuesOptimizable, normalized.get(0)
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
        Function<? super Random, SmodelIntegerChromosome> func = (Random r) -> {
            return SmodelIntegerChromosome.of(IntRange.of(0, 1), optimizable);
        };
        SmodelIntegerChromosome actualChromosome = RandomRegistry.with(new Random(42), func);

        OptimizableValue<?> actualValue = optimizableNormalizer.toOptimizable(actualChromosome);

        assertEquals(false, actualValue.getValue());
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
        Function<? super Random, SmodelIntegerChromosome> func = (Random r) -> {
            return SmodelIntegerChromosome.of(IntRange.of(0, 1), optimizable);
        };
        SmodelIntegerChromosome actualChromosome = RandomRegistry.with(new Random(42), func);

        OptimizableValue<?> actualValue = optimizableNormalizer.toOptimizable(actualChromosome);

        assertEquals(false, actualValue.getValue());
        assertSame(actualValue.getOptimizable(), optimizable);
    }

//
//    @Test
//    public void testToOptimizableIntIndex0() {
//        IntLiteral literal1 = smodelCreator.createIntLiteral(1);
//        IntLiteral literal2 = smodelCreator.createIntLiteral(2);
//        when(calculator.calculateInteger(literal1)).thenReturn(literal1.getValue());
//        when(calculator.calculateInteger(literal2)).thenReturn(literal2.getValue());
//        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
//        Optimizable optimizable = smodelCreator.createOptimizable("optimizable", DataType.INT, bounds);
//        SmodelBitset bitset = new SmodelBitset(1);
//        SmodelBitChromosome actualChromosome = SmodelBitChromosome.of(bitset, optimizable, 2,
//                new BinaryBitInterpreter());
//
//        OptimizableValue<?> actualValue = optimizableNormalizer.toOptimizable(actualChromosome);
//
//        assertEquals(1, actualValue.getValue());
//        assertSame(actualValue.getOptimizable(), optimizable);
//    }
//
//    @Test
//    public void testToOptimizableIntIndex1() {
//        IntLiteral literal1 = smodelCreator.createIntLiteral(1);
//        IntLiteral literal2 = smodelCreator.createIntLiteral(2);
//        when(calculator.calculateInteger(literal1)).thenReturn(literal1.getValue());
//        when(calculator.calculateInteger(literal2)).thenReturn(literal2.getValue());
//        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
//        Optimizable optimizable = smodelCreator.createOptimizable("optimizable", DataType.INT, bounds);
//        SmodelBitset bitset = new SmodelBitset(1);
//        bitset.set(0);
//        SmodelBitChromosome initialChromosome = SmodelBitChromosome.of(bitset, optimizable, 2,
//                new BinaryBitInterpreter());
//
//        OptimizableValue<?> actualValue = optimizableNormalizer.toOptimizable(initialChromosome);
//
//        assertEquals(2, actualValue.getValue());
//        assertSame(actualValue.getOptimizable(), optimizable);
//    }
//
//    @Test
//    public void testToOptimizableDoubleIndex1() {
//        DoubleLiteral literal1 = smodelCreator.createDoubleLiteral(1);
//        DoubleLiteral literal2 = smodelCreator.createDoubleLiteral(2);
//        when(calculator.calculateDouble(literal1)).thenReturn(literal1.getValue());
//        when(calculator.calculateDouble(literal2)).thenReturn(literal2.getValue());
//        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
//        Optimizable optimizable = smodelCreator.createOptimizable("optimizable", DataType.DOUBLE, bounds);
//        SmodelBitset bitset = new SmodelBitset(1);
//        bitset.set(0);
//        SmodelBitChromosome initialChromosome = SmodelBitChromosome.of(bitset, optimizable, 2,
//                new BinaryBitInterpreter());
//
//        OptimizableValue<?> actualValue = optimizableNormalizer.toOptimizable(initialChromosome);
//
//        assertEquals(2.0, actualValue.getValue());
//        assertSame(actualValue.getOptimizable(), optimizable);
//    }
//
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
        Function<? super Random, List<SmodelIntegerChromosome>> func = (Random r) -> {
            return optimizableNormalizer.toNormalized(optimizableList);
        };
        List<SmodelIntegerChromosome> normalized = RandomRegistry.with(new Random(42), func);

        List<OptimizableValue<?>> optimizableValues = optimizableNormalizer.toOptimizableValues(normalized);

        assertEquals(2, optimizableValues.size());
        assertEquals(twoValuesOptimizable, optimizableValues.get(0)
            .getOptimizable());
        assertEquals(singleValueOptimizable, optimizableValues.get(1)
            .getOptimizable());
        assertEquals(2.0, optimizableValues.get(0)
            .getValue());
        assertEquals(3.0, optimizableValues.get(1)
            .getValue());
    }

//
//    @Test
//    public void testToOptimizableStringIndex1() {
//        StringLiteral literal1 = smodelCreator.createStringLiteral("hello");
//        StringLiteral literal2 = smodelCreator.createStringLiteral("World");
//        when(calculator.calculateString(literal1)).thenReturn(literal1.getValue());
//        when(calculator.calculateString(literal2)).thenReturn(literal2.getValue());
//        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
//        Optimizable optimizable = smodelCreator.createOptimizable("optimizable", DataType.STRING, bounds);
//        SmodelBitset bitset = new SmodelBitset(1);
//        bitset.set(0);
//        SmodelBitChromosome initialChromosome = SmodelBitChromosome.of(bitset, optimizable, 2,
//                new BinaryBitInterpreter());
//
//        OptimizableValue<?> actualValue = optimizableNormalizer.toOptimizable(initialChromosome);
//
//        assertEquals("World", actualValue.getValue());
//        assertSame(actualValue.getOptimizable(), optimizable);
//    }
//
    @Test
    public void parseOptimizablesTest() {
        // Arrange
        SetBounds boolBound = setBoundsHelper.initializeBooleanSetBound(smodelCreator, List.of(true, false),
                calculator);
        when(boolOptimizable.getValues()).thenReturn(boolBound);
        when(boolOptimizable.getDataType()).thenReturn(DataType.BOOL);
        SetBounds intBound = setBoundsHelper.initializeIntegerSetBound(smodelCreator, smallIntList, calculator);
        when(intOptimizable.getValues()).thenReturn(intBound);
        when(intOptimizable.getDataType()).thenReturn(DataType.INT);
        List<Optimizable> optimizables = new ArrayList<>();
        optimizables.add(boolOptimizable);
        optimizables.add(intOptimizable);

        // Act
        List<SmodelIntegerChromosome> parsedOptimizables = optimizableNormalizer.toNormalized(optimizables);

        // Assert
        SmodelIntegerChromosome firstOptimizable = parsedOptimizables.get(0);
        assertEquals(firstOptimizable.getOptimizable(), boolOptimizable);
        RandomRegistry.with(new Random(42), (r) -> {
            SmodelIntegerChromosome newInstance = firstOptimizable.newInstance();
            assertEquals(1, newInstance.intValue());
            return "";
        });
        SmodelIntegerChromosome secondOptimizable = parsedOptimizables.get(1);
        assertEquals(secondOptimizable.getOptimizable(), intOptimizable);
        RandomRegistry.with(new Random(42), (r) -> {
            SmodelIntegerChromosome newInstance = secondOptimizable.newInstance();
            assertEquals(1, newInstance.intValue());
            return "";
        });
    }
//
//    @SuppressWarnings("unchecked")
//    @Test
//    public void testToGenotypeBoolean() {
//        SetBounds setBound = setBoundsHelper.initializeBooleanSetBound(smodelCreator, List.of(true, false), calculator);
//        when(optimizable.getDataType()).thenReturn(DataType.BOOL);
//        when(optimizable.getValues()).thenReturn(setBound);
//
//        SmodelBitChromosome genotype = optimizableNormalizer.toNormalized(optimizable);
//
//        checkBooleanGenotype(genotype);
//    }
//
//    @SuppressWarnings("unchecked")
//    @Test
//    public void testToGenotypeIntegerSet() {
//        SetBounds setBound = setBoundsHelper.initializeIntegerSetBound(smodelCreator, smallIntList, calculator);
//        when(optimizable.getDataType()).thenReturn(DataType.INT);
//        when(optimizable.getValues()).thenReturn(setBound);
//
//        SmodelBitChromosome normalized = optimizableNormalizer.toNormalized(optimizable);
//
//        assertEquals(optimizable, normalized.getOptimizable());
//        RandomRegistry.with(new Random(42), (r) -> {
//            SmodelBitChromosome newInstance = normalized.newInstance();
//            assertEquals(1, newInstance.intValue());
//            return "";
//        });
//
//    }
//
//    @Test
//    public void testToGenotypeIntegerRange() {
//        RangeBounds rangeBound = new RangeBoundsHelper().initializeIntegerRangeBound(smodelCreator, calculator, 0, 20,
//                1);
//        when(optimizable.getDataType()).thenReturn(DataType.INT);
//        when(optimizable.getValues()).thenReturn(rangeBound);
//
//        SmodelBitChromosome normalized = optimizableNormalizer.toNormalized(optimizable);
//
//        assertEquals(optimizable, normalized.getOptimizable());
//        RandomRegistry.with(new Random(42), (r) -> {
//            SmodelBitChromosome newInstance = normalized.newInstance();
//            assertEquals(10, newInstance.intValue());
//            return "";
//        });
//    }
//
//    @Test
//    public void testToGenotypeDoubleSet() {
//        SetBounds setBound = setBoundsHelper.initializeDoubleSetBound(smodelCreator,
//                List.of(1.0, 2.0, 5.0, 6.5, 8.73651, 2.0, 9.0), calculator);
//        when(optimizable.getDataType()).thenReturn(DataType.DOUBLE);
//        when(optimizable.getValues()).thenReturn(setBound);
//
//        SmodelBitChromosome genotype = optimizableNormalizer.toNormalized(optimizable);
//
//        assertEquals(optimizable, genotype.getOptimizable());
//        RandomRegistry.with(new Random(42), (r) -> {
//            SmodelBitChromosome newInstance = genotype.newInstance();
//            assertEquals(1, newInstance.intValue());
//            return "";
//        });
//    }
//
//    @Test
//    public void testToGenotypeDoubleRange() {
//        RangeBounds rangeBound = new RangeBoundsHelper().initializeDoubleRangeBound(smodelCreator, calculator, 0.0,
//                20.0, 1.0);
//        when(optimizable.getDataType()).thenReturn(DataType.DOUBLE);
//        when(optimizable.getValues()).thenReturn(rangeBound);
//
//        SmodelBitChromosome genotype = optimizableNormalizer.toNormalized(optimizable);
//
//        assertEquals(optimizable, genotype.getOptimizable());
//        RandomRegistry.with(new Random(42), (r) -> {
//            SmodelBitChromosome newInstance = genotype.newInstance();
//            assertEquals(10, newInstance.intValue());
//            return "";
//        });
//    }
//
//    @SuppressWarnings("unchecked")
//    @Test
//    public void testToGenotypeString() {
//        SetBounds setBounds = new SetBoundsHelper().initializeStringSetBound(smodelCreator,
//                List.of("Hello", "World", "!"), calculator);
//        Optimizable optimizable = smodelCreator.createOptimizable("stringSetOptimizable", DataType.STRING, setBounds);
//
//        SmodelBitChromosome genotype = optimizableNormalizer.toNormalized(optimizable);
//
//        assertEquals(optimizable, genotype.getOptimizable());
//        RandomRegistry.with(new Random(42), (r) -> {
//            SmodelBitChromosome newInstance = genotype.newInstance();
//            assertEquals(2, newInstance.intValue());
//            OptimizableValue<?> actualValue = optimizableNormalizer.toOptimizable(newInstance);
//            assertEquals("!", actualValue.getValue());
//            return "";
//        });
//    }
//
//    private void checkBooleanGenotype(SmodelBitChromosome chromosome) {
//        RandomRegistry.with(new Random(42), (r) -> {
//            SmodelBitChromosome newInstance = chromosome.newInstance();
//            assertEquals(1, newInstance.intValue());
//            return "";
//        });
//    }

}
