package org.palladiosimulator.simexp.dsl.ea.optimizer.representation;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class SmodelBitsetTest {
    private SmodelBitset bitSet;

    @Before
    public void setUp() {
        bitSet = new SmodelBitset(3);
    }

    @Test
    public void testToInt0() {
        int actualInt = bitSet.toInt();

        assertEquals(0, actualInt);
    }

    @Test
    public void testToInt1() {
        bitSet.set(0);

        int actualInt = bitSet.toInt();

        assertEquals(1, actualInt);
    }

    @Test
    public void testToInt2() {
        bitSet.set(1);

        int actualInt = bitSet.toInt();

        assertEquals(2, actualInt);
    }

    @Test
    public void testToInt3() {
        bitSet.set(0);
        bitSet.set(1);

        int actualInt = bitSet.toInt();

        assertEquals(3, actualInt);
    }

    @Test
    public void testFromInt1() {
        SmodelBitset expectedBitSet = new SmodelBitset(3, new BinaryBitInterpreter());
        expectedBitSet.set(0);

        SmodelBitset bitSet = SmodelBitset.fromInt(1, 1, new BinaryBitInterpreter());

        assertEquals(expectedBitSet, bitSet);
    }

    @Test
    public void testFromInt2() {
        SmodelBitset expectedBitSet = new SmodelBitset(3, new BinaryBitInterpreter());
        expectedBitSet.set(0);
        expectedBitSet.set(1);
        expectedBitSet.set(3);

        SmodelBitset bitSet = SmodelBitset.fromInt(4, 11, new BinaryBitInterpreter());

        assertEquals(expectedBitSet, bitSet);
    }

    @Test
    public void testGrayToInt() {
        SmodelBitset grayBitSet = new SmodelBitset(4, new GrayBitInterpreter());
        grayBitSet.set(1);
        grayBitSet.set(2);
        grayBitSet.set(3);

        int actualInt = grayBitSet.toInt();

        assertEquals(11, actualInt);
    }

    @Test
    public void testGrayFromInt() {
        SmodelBitset expectedBitSet = new SmodelBitset(3, new GrayBitInterpreter());
        expectedBitSet.set(1);
        expectedBitSet.set(2);
        expectedBitSet.set(3);

        SmodelBitset bitSet = SmodelBitset.fromInt(4, 11, new GrayBitInterpreter());

        assertEquals(expectedBitSet, bitSet);
    }
}
