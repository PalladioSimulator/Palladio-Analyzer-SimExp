package org.palladiosimulator.simexp.dsl.ea.optimizer.representation;

import static org.junit.Assert.assertEquals;

import java.util.BitSet;

import org.junit.Before;
import org.junit.Test;

public class BinaryBitInterpreterTest {

    private BinaryBitInterpreter interpreter;

    @Before
    public void setUp() {
        interpreter = new BinaryBitInterpreter();
    }

    @Test
    public void testBinaryToIdx0() {
        String bitString = "0";
        SmodelBitset bitSet = idxToBitSet(bitString);

        int actualIdx = interpreter.toInt(bitSet);

        assertEquals(0, actualIdx);
    }

    @Test
    public void testBinaryToIdx1() {
        String bitString = "1";
        SmodelBitset bitSet = idxToBitSet(bitString);

        int actualIdx = interpreter.toInt(bitSet);

        assertEquals(1, actualIdx);
    }

    @Test
    public void testBinaryToIdx5() {
        String bitString = "0111";
        SmodelBitset bitSet = idxToBitSet(bitString);

        int actualIdx = interpreter.toInt(bitSet);

        assertEquals(7, actualIdx);
    }

    @Test
    public void testBinaryToIdx99() {
        String bitString = "1010010";
        SmodelBitset bitSet = idxToBitSet(bitString);

        int actualIdx = interpreter.toInt(bitSet);

        assertEquals(82, actualIdx);
    }

    @Test
    public void testIdxToBinary0() {
        int value = 0;

        BitSet bitSet = interpreter.toBitSet(value);

        String binaryString = bitSetToString(bitSet);
        assertEquals("0", binaryString);
    }

    @Test
    public void testIdxToBinary1() {
        int value = 1;

        BitSet bitSet = interpreter.toBitSet(value);

        String binaryString = bitSetToString(bitSet);
        assertEquals("1", binaryString);
    }

    @Test
    public void testIdxToBinary5() {
        int value = 5;

        BitSet bitSet = interpreter.toBitSet(value);

        String binaryString = bitSetToString(bitSet);
        assertEquals("101", binaryString);
    }

    @Test
    public void testIdxToBinary99() {
        int value = 99;

        BitSet bitSet = interpreter.toBitSet(value);

        String binaryString = bitSetToString(bitSet);
        assertEquals("1100011", binaryString);
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
        SmodelBitset bitSet = new SmodelBitset(first.length(), new BinaryBitInterpreter());
        int stringLength = first.length();
        for (int i = 0; i < stringLength; i++) {
            if (first.charAt(stringLength - 1 - i) == '1') {
                bitSet.set(i);
            }
        }
        return bitSet;
    }
}
