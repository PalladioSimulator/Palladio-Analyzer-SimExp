package org.palladiosimulator.simexp.dsl.ea.optimizer.representation;

import static org.junit.Assert.assertEquals;

import java.util.BitSet;

import org.junit.Before;
import org.junit.Test;

public class IntToGrayConverterTest {

    private IntToGrayConverter grayConverter;

    @Before
    public void setUp() {
        grayConverter = new IntToGrayConverter();
    }

    @Test
    public void testGrayToIdx0() {
        String bitString = "0";
        BitSet bitSet = idxToBitSet(bitString);

        int actualIdx = grayConverter.grayToIdx(bitSet);

        assertEquals(0, actualIdx);
    }

    @Test
    public void testGrayToIdx1() {
        String bitString = "1";
        BitSet bitSet = idxToBitSet(bitString);

        int actualIdx = grayConverter.grayToIdx(bitSet);

        assertEquals(1, actualIdx);
    }

    @Test
    public void testGrayToIdx5() {
        String bitString = "0111";
        BitSet bitSet = idxToBitSet(bitString);

        int actualIdx = grayConverter.grayToIdx(bitSet);

        assertEquals(5, actualIdx);
    }

    @Test
    public void testGrayToIdx99() {
        String bitString = "1010010";
        BitSet bitSet = idxToBitSet(bitString);

        int actualIdx = grayConverter.grayToIdx(bitSet);

        assertEquals(99, actualIdx);
    }

    @Test
    public void testIdxToGray0() {
        int value = 0;
        int bitSetLength = getBitSetLength(value);

        BitSet actualBitSet = grayConverter.idxToGray(value, bitSetLength);

        String grayString = bitSetToString(actualBitSet);
        assertEquals("0", grayString);
    }

    @Test
    public void testIdxToGray1() {
        int value = 1;
        int bitSetLength = getBitSetLength(value);

        BitSet actualBitSet = grayConverter.idxToGray(value, bitSetLength);

        String grayString = bitSetToString(actualBitSet);
        assertEquals("1", grayString);
    }

    @Test
    public void testIdxToGray5() {
        int value = 5;
        int bitSetLength = getBitSetLength(value);

        BitSet actualBitSet = grayConverter.idxToGray(value, bitSetLength);

        String grayString = bitSetToString(actualBitSet);
        assertEquals("111", grayString);
    }

    @Test
    public void testIdxToGray99() {
        int value = 99;
        int bitSetLength = getBitSetLength(value);

        BitSet actualBitSet = grayConverter.idxToGray(value, bitSetLength);

        String grayString = bitSetToString(actualBitSet);
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

    private int getBitSetLength(int idx) {
        return (int) Math.ceil(Math.log(5) / Math.log(2));
    }

    private BitSet idxToBitSet(String first) {
        BitSet bitSet = new BitSet();
        int stringLength = first.length();
        for (int i = 0; i < stringLength; i++) {
            if (first.charAt(stringLength - 1 - i) == '1') {
                bitSet.set(i);
            }
        }
        return bitSet;
    }

}
