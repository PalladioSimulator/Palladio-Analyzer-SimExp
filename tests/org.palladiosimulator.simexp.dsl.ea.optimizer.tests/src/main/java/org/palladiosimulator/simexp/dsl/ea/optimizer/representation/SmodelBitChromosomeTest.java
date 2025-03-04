package org.palladiosimulator.simexp.dsl.ea.optimizer.representation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DataType;
import org.palladiosimulator.simexp.dsl.smodel.smodel.DoubleLiteral;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Literal;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Optimizable;
import org.palladiosimulator.simexp.dsl.smodel.smodel.RangeBounds;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SetBounds;
import org.palladiosimulator.simexp.dsl.smodel.test.util.SmodelCreator;

import io.jenetics.BitGene;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;

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
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(bitset, optimizable, 1, new BinaryBitInterpreter());

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
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(bitset, optimizable, 1, new BinaryBitInterpreter());

        int actualIntValue = chromosome.intValue();

        assertEquals(1, actualIntValue);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidLength() {
        Literal literal1 = smodelCreator.createIntLiteral(1);
        Literal literal2 = smodelCreator.createIntLiteral(2);
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        optimizable = smodelCreator.createOptimizable("optimizable", DataType.INT, bounds);
        SmodelBitset bitset = new SmodelBitset(1);
        bitset.set(0);

        SmodelBitChromosome.of(bitset, optimizable, 0, new BinaryBitInterpreter());
    }

    @Test
    public void testDoubleValue0() {
        Literal literal1 = smodelCreator.createDoubleLiteral(1);
        Literal literal2 = smodelCreator.createDoubleLiteral(2);
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        optimizable = smodelCreator.createOptimizable("optimizable", DataType.DOUBLE, bounds);
        SmodelBitset bitset = new SmodelBitset(1);
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(bitset, optimizable, 1, new BinaryBitInterpreter());

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
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(bitset, optimizable, 9, new BinaryBitInterpreter());

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
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(bitset, optimizable, 9, new BinaryBitInterpreter());

        assertFalse(chromosome.isValid());
    }

    @Test
    public void testIsValidFalse2() {
        DoubleLiteral start = smodelCreator.createDoubleLiteral(1);
        DoubleLiteral end = smodelCreator.createDoubleLiteral(10);
        DoubleLiteral step = smodelCreator.createDoubleLiteral(1);
        RangeBounds bounds = smodelCreator.createRangeBounds(start, end, step);
        optimizable = smodelCreator.createOptimizable("optimizable", DataType.DOUBLE, bounds);
        SmodelBitset bitset = new SmodelBitset(4);
        bitset.set(0);
        bitset.set(3);
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(bitset, optimizable, 9, new BinaryBitInterpreter());

        assertFalse(chromosome.isValid());
    }

    @Test
    public void testToBitSet() {
        DoubleLiteral start = smodelCreator.createDoubleLiteral(1);
        DoubleLiteral end = smodelCreator.createDoubleLiteral(10);
        DoubleLiteral step = smodelCreator.createDoubleLiteral(1);
        RangeBounds bounds = smodelCreator.createRangeBounds(start, end, step);
        optimizable = smodelCreator.createOptimizable("optimizable", DataType.DOUBLE, bounds);
        SmodelBitset bitset = new SmodelBitset(4);
        bitset.set(0);
        bitset.set(3);
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(bitset, optimizable, 9, new BinaryBitInterpreter());

        SmodelBitset resultBitset = chromosome.toBitSet();

        assertEquals(bitset, resultBitset);
    }

    @Test
    public void testToEmptyBitSet() {
        DoubleLiteral start = smodelCreator.createDoubleLiteral(1);
        DoubleLiteral end = smodelCreator.createDoubleLiteral(10);
        DoubleLiteral step = smodelCreator.createDoubleLiteral(1);
        RangeBounds bounds = smodelCreator.createRangeBounds(start, end, step);
        optimizable = smodelCreator.createOptimizable("optimizable", DataType.DOUBLE, bounds);
        SmodelBitset bitset = new SmodelBitset(4);
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(bitset, optimizable, 9, new BinaryBitInterpreter());

        SmodelBitset resultBitset = chromosome.toBitSet();

        assertEquals(bitset, resultBitset);
    }

    @Test
    public void testNewInstance() {
        DoubleLiteral start = smodelCreator.createDoubleLiteral(1);
        DoubleLiteral end = smodelCreator.createDoubleLiteral(10);
        DoubleLiteral step = smodelCreator.createDoubleLiteral(1);
        RangeBounds bounds = smodelCreator.createRangeBounds(start, end, step);
        optimizable = smodelCreator.createOptimizable("optimizable", DataType.DOUBLE, bounds);
        SmodelBitset bitset = new SmodelBitset(4);
        bitset.set(0);
        bitset.set(3);
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(bitset, optimizable, 9, new BinaryBitInterpreter());
        Function<? super Random, SmodelBitChromosome> optFunction = (Random r) -> {
            return chromosome.newInstance();
        };
        SmodelBitset expectedBitset = new SmodelBitset(4);
        expectedBitset.set(1);

        SmodelBitChromosome newInstance = RandomRegistry.with(new Random(30), optFunction);

        assertEquals(expectedBitset, newInstance.toBitSet());
        assertEquals(2, newInstance.intValue());
        assertTrue(newInstance.isValid());
        assertEquals(optimizable, newInstance.getOptimizable());
    }

    @Test
    public void testNewInstanceLengthZero() {
        DoubleLiteral start = smodelCreator.createDoubleLiteral(1);
        DoubleLiteral end = smodelCreator.createDoubleLiteral(1);
        DoubleLiteral step = smodelCreator.createDoubleLiteral(1);
        RangeBounds bounds = smodelCreator.createRangeBounds(start, end, step);
        optimizable = smodelCreator.createOptimizable("optimizable", DataType.DOUBLE, bounds);
        SmodelBitset bitset = new SmodelBitset(8);
        bitset.set(0);
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(bitset, optimizable, 1, new BinaryBitInterpreter());
        Function<? super Random, SmodelBitChromosome> optFunction = (Random r) -> {
            return chromosome.newInstance();
        };
        SmodelBitset expectedBitset = new SmodelBitset(8);

        SmodelBitChromosome newInstance = RandomRegistry.with(new Random(30), optFunction);

        assertEquals(expectedBitset, newInstance.toBitSet());
        assertEquals(8, newInstance.toBitSet()
            .getNbits());
        assertEquals(0, newInstance.intValue());
        assertTrue(newInstance.isValid());
        assertEquals(optimizable, newInstance.getOptimizable());
    }

    @Test
    public void testNewInstanceWithExistingGenes() {
        Literal literal1 = smodelCreator.createDoubleLiteral(1);
        Literal literal2 = smodelCreator.createDoubleLiteral(2);
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        optimizable = smodelCreator.createOptimizable("optimizable", DataType.DOUBLE, bounds);
        SmodelBitset bitset = new SmodelBitset(8);
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(bitset, optimizable, 9, new BinaryBitInterpreter());
        BitGene firstGene = BitGene.TRUE;
        BitGene secondGene = BitGene.FALSE;
        BitGene thirdGene = BitGene.TRUE;
        SmodelBitset expectedBitset = new SmodelBitset(4);
        expectedBitset.set(0);
        expectedBitset.set(2);
        ISeq<BitGene> geneSeq = ISeq.of(firstGene, secondGene, thirdGene);
        Function<? super Random, SmodelBitChromosome> optFunction = (Random r) -> {
            return chromosome.newInstance(geneSeq);
        };

        SmodelBitChromosome newInstance = RandomRegistry.with(new Random(30), optFunction);

        assertEquals(expectedBitset, newInstance.toBitSet());
        assertEquals(5, newInstance.intValue());
        assertEquals(0.666666, newInstance.oneProbability(), 0.00001);
        assertTrue(newInstance.isValid());
        assertEquals(optimizable, newInstance.getOptimizable());
    }

    @Test
    public void testNewInstanceInvalidWithExistingGenes() {
        Literal literal1 = smodelCreator.createDoubleLiteral(1);
        Literal literal2 = smodelCreator.createDoubleLiteral(2);
        SetBounds bounds = smodelCreator.createSetBounds(literal1, literal2);
        optimizable = smodelCreator.createOptimizable("optimizable", DataType.DOUBLE, bounds);
        SmodelBitset bitset = new SmodelBitset(8);
        SmodelBitChromosome chromosome = SmodelBitChromosome.of(bitset, optimizable, 2, new BinaryBitInterpreter());
        BitGene firstGene = BitGene.TRUE;
        BitGene secondGene = BitGene.FALSE;
        BitGene thirdGene = BitGene.TRUE;
        SmodelBitset expectedBitset = new SmodelBitset(4);
        expectedBitset.set(0);
        expectedBitset.set(2);
        ISeq<BitGene> geneSeq = ISeq.of(firstGene, secondGene, thirdGene);
        Function<? super Random, SmodelBitChromosome> optFunction = (Random r) -> {
            return chromosome.newInstance(geneSeq);
        };

        SmodelBitChromosome newInstance = RandomRegistry.with(new Random(30), optFunction);

        assertEquals(expectedBitset, newInstance.toBitSet());
        assertEquals(5, newInstance.intValue());
        assertEquals(0.666666, newInstance.oneProbability(), 0.00001);
        assertFalse(newInstance.isValid());
        assertEquals(optimizable, newInstance.getOptimizable());
    }

}
