package org.palladiosimulator.simexp.dsl.ea.optimizer.representation;

import static org.junit.Assert.assertEquals;

import java.util.BitSet;

import org.junit.Before;
import org.junit.Test;

public class GrayBitInterpreterTest {

    private GrayBitInterpreter interpreter;

    @Before
    public void setUp() {
        interpreter = new GrayBitInterpreter();
    }

    @Test
    public void testGrayToIdx0() {
        String bitString = "0";
        SmodelBitset bitSet = idxToBitSet(bitString);

        int actualIdx = interpreter.toInt(bitSet);

        assertEquals(0, actualIdx);
    }

    @Test
    public void testGrayToIdx1() {
        String bitString = "1";
        SmodelBitset bitSet = idxToBitSet(bitString);

        int actualIdx = interpreter.toInt(bitSet);

        assertEquals(1, actualIdx);
    }

    @Test
    public void testGrayToIdx5() {
        String bitString = "0111";
        SmodelBitset bitSet = idxToBitSet(bitString);

        int actualIdx = interpreter.toInt(bitSet);

        assertEquals(5, actualIdx);
    }

    @Test
    public void testGrayToIdx99() {
        String bitString = "1010010";
        SmodelBitset bitSet = idxToBitSet(bitString);

        int actualIdx = interpreter.toInt(bitSet);

        assertEquals(99, actualIdx);
    }

    @Test
    public void testIdxToGray0() {
        int value = 0;

        BitSet bitSet = interpreter.toBitSet(value);

        String grayString = bitSetToString(bitSet);
        assertEquals("0", grayString);
    }

    @Test
    public void testIdxToGray1() {
        int value = 1;

        BitSet bitSet = interpreter.toBitSet(value);

        String grayString = bitSetToString(bitSet);
        assertEquals("1", grayString);
    }

    @Test
    public void testIdxToGray5() {
        int value = 5;

        BitSet bitSet = interpreter.toBitSet(value);

        String grayString = bitSetToString(bitSet);
        assertEquals("111", grayString);
    }

    @Test
    public void testIdxToGray99() {
        int value = 99;

        BitSet bitSet = interpreter.toBitSet(value);

        String grayString = bitSetToString(bitSet);
        assertEquals("1010010", grayString);
    }

    private String bitSetToString(BitSet firstBitSet) {
        StringBuilder builder = new StringBuilder();
        if (firstBitSet.length() > 0) {
            for (int i = firstBitSet.length() - 1; i >= 0; i--) {
                if (firstBitSet.get(i)) {
                    builder.append('1');
                } else {
                    builder.append('0');
                }
            }
        } else {
            builder.append('0');
        }
        return builder.toString();
    }

    private SmodelBitset idxToBitSet(String first) {
        SmodelBitset bitSet = new SmodelBitset(first.length());
        int stringLength = first.length();
        for (int i = 0; i < stringLength; i++) {
            if (first.charAt(stringLength - 1 - i) == '1') {
                bitSet.set(i);
            }
        }
        return bitSet;
    }

}
