package org.palladiosimulator.simexp.dsl.ea.optimizer.representation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DoubleLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Literal;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

public class SmodelBitChromosomeTest {
    private SmodelCreator smodelCreator;

    private Optimizable optimizable;

    @Before
    public void setUp() {
        smodelCreator = new SmodelCreator();

    }

    @Test
    public void testIntValue0() {
        Literal literal1 = smodelCreator.createIntLiteral(1);
        Literal literal2 = smodelCreator.createIntLiteral(2);
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        optimizable = smodelCreator.createOptimizable("optimizable", DataType.INT, bounds);
        SmodelBitset bitset = new SmodelBitset(1);
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(bitset, optimizable, 1);

        int actualIntValue = chromosome.intValue();

        assertEquals(0, actualIntValue);
    }

    @Test
    public void testIntValue1() {
        Literal literal1 = smodelCreator.createIntLiteral(1);
        Literal literal2 = smodelCreator.createIntLiteral(2);
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        optimizable = smodelCreator.createOptimizable("optimizable", DataType.INT, bounds);
        SmodelBitset bitset = new SmodelBitset(1);
        bitset.set(0);
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(bitset, optimizable, 1);

        int actualIntValue = chromosome.intValue();

        assertEquals(1, actualIntValue);
    }

    @Test
    public void testDoubleValue0() {
        Literal literal1 = smodelCreator.createDoubleLiteral(1);
        Literal literal2 = smodelCreator.createDoubleLiteral(2);
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        optimizable = smodelCreator.createOptimizable("optimizable", DataType.DOUBLE, bounds);
        SmodelBitset bitset = new SmodelBitset(1);
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(bitset, optimizable, 1);

        int actualIntValue = chromosome.intValue();

        assertEquals(0, actualIntValue);
    }

    @Test
    public void testIsValid() {
        DoubleLiteral start = smodelCreator.createDoubleLiteral(1);
        DoubleLiteral end = smodelCreator.createDoubleLiteral(10);
        DoubleLiteral step = smodelCreator.createDoubleLiteral(1);
        RangeBounds bounds = smodelCreator.createRangeBounds(start, end, step);
        optimizable = smodelCreator.createOptimizable("optimizable", DataType.DOUBLE, bounds);
        SmodelBitset bitset = new SmodelBitset(4);
        bitset.set(1);
        bitset.set(2);
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(bitset, optimizable, 9);

        assertTrue(chromosome.isValid());
    }

    @Test
    public void testIsValidFalse() {
        DoubleLiteral start = smodelCreator.createDoubleLiteral(1);
        DoubleLiteral end = smodelCreator.createDoubleLiteral(10);
        DoubleLiteral step = smodelCreator.createDoubleLiteral(1);
        RangeBounds bounds = smodelCreator.createRangeBounds(start, end, step);
        optimizable = smodelCreator.createOptimizable("optimizable", DataType.DOUBLE, bounds);
        SmodelBitset bitset = new SmodelBitset(4);
        bitset.set(1);
        bitset.set(3);
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(bitset, optimizable, 9);

        assertFalse(chromosome.isValid());
    }

    // TODO nbruening: Add remaining tests
}
