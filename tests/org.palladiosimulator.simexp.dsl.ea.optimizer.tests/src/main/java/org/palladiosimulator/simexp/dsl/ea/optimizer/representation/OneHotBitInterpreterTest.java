package org.palladiosimulator.simexp.dsl.ea.optimizer.representation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.OptimizableProcessingException;

public class OneHotBitInterpreterTest {

    private OneHotBitInterpreter interpreter;

    @Before
    public void setUp() {
        interpreter = new OneHotBitInterpreter();
    }

    @Test
    public void testOHToIdx0() {
        SmodelBitset bitSet = new SmodelBitset(1, new OneHotBitInterpreter());
        bitSet.set(0);

        int actualIdx = interpreter.toInt(bitSet);

        assertEquals(0, actualIdx);
    }

    @Test
    public void testGrayToIdx1() {
        SmodelBitset bitSet = new SmodelBitset(3, new OneHotBitInterpreter());
        bitSet.set(1);

        int actualIdx = interpreter.toInt(bitSet);

        assertEquals(1, actualIdx);
    }

    @Test(expected = OptimizableProcessingException.class)
    public void testMultipleBitsSet() {
        String bitString = "0111";
        SmodelBitset bitSet = new SmodelBitset(4);
        bitSet.set(0, 3);

        int actualIdx = interpreter.toInt(bitSet);
    }

    @Test
    public void testGrayToIdx5() {
        SmodelBitset bitSet = new SmodelBitset(6, new OneHotBitInterpreter());
        bitSet.set(5);

        int actualIdx = interpreter.toInt(bitSet);

        assertEquals(5, actualIdx);
    }

    @Test
    public void testOHToIdx99() {
        SmodelBitset bitSet = new SmodelBitset(100);
        bitSet.set(99);

        int actualIdx = interpreter.toInt(bitSet);

        assertEquals(99, actualIdx);
    }

    @Test
    public void testIdxToOH() {
        int value = 0;

        BitSet bitSet = interpreter.toBitSet(value);

        List<Integer> setBits = new ArrayList<>();
        for (int i = 0; i < bitSet.length(); i++) {
            if (bitSet.get(i))
                setBits.add(i);
        }
        assertEquals(1, setBits.size());
        assertTrue(setBits.contains(0));
    }

    @Test
    public void testIdxToBOH1() {
        int value = 1;

        BitSet bitSet = interpreter.toBitSet(value);

        List<Integer> setBits = new ArrayList<>();
        for (int i = 0; i < bitSet.length(); i++) {
            if (bitSet.get(i))
                setBits.add(i);
        }
        assertEquals(1, setBits.size());
        assertTrue(setBits.contains(1));
    }

    @Test
    public void testIdxToOH5() {
        int value = 5;

        BitSet bitSet = interpreter.toBitSet(value);

        List<Integer> setBits = new ArrayList<>();
        for (int i = 0; i < bitSet.length(); i++) {
            if (bitSet.get(i))
                setBits.add(i);
        }
        assertEquals(1, setBits.size());
        assertTrue(setBits.contains(5));
    }

    @Test
    public void testIdxToOH99() {
        int value = 99;

        BitSet bitSet = interpreter.toBitSet(value);

        List<Integer> setBits = new ArrayList<>();
        for (int i = 0; i < bitSet.length(); i++) {
            if (bitSet.get(i))
                setBits.add(i);
        }
        assertEquals(1, setBits.size());
        assertTrue(setBits.contains(99));
    }

    @Test
    public void testOHIsValid1() {
        SmodelBitset bitSet = new SmodelBitset(3, new OneHotBitInterpreter());
        bitSet.set(1);

        boolean valid = interpreter.isValid(bitSet, 3);

        assertTrue(valid);
    }

    @Test
    public void testOHIsValid2() {
        SmodelBitset bitSet = new SmodelBitset(3, new OneHotBitInterpreter());
        bitSet.set(2);

        boolean valid = interpreter.isValid(bitSet, 3);

        assertTrue(valid);
    }

    @Test
    public void testOHIsInValidTooManyBitsSet() {
        SmodelBitset bitSet = new SmodelBitset(3, new OneHotBitInterpreter());
        bitSet.set(0);
        bitSet.set(2);

        boolean valid = interpreter.isValid(bitSet, 3);

        assertFalse(valid);
    }

    @Test
    public void testOHIsInValidNoBitsSet() {
        SmodelBitset bitSet = new SmodelBitset(3, new OneHotBitInterpreter());

        boolean valid = interpreter.isValid(bitSet, 3);

        assertFalse(valid);
    }

    @Test
    public void testOHIsInValidTooHighBitSet() {
        SmodelBitset bitSet = new SmodelBitset(4, new OneHotBitInterpreter());
        bitSet.set(3);

        boolean valid = interpreter.isValid(bitSet, 3);

        assertFalse(valid);
    }

}
