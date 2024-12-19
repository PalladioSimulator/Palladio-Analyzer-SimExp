package org.palladiosimulator.simexp.dsl.ea.optimizer.representation;

import static org.junit.Assert.assertEquals;

import java.util.BitSet;

import org.junit.Test;

public class GrayConverterHelperTest {

    private GrayConverterHelper grayConverterHelper = new GrayConverterHelper();

    @Test
    public void testGrayToIdx() {
        String first = "0111";
        String second = "1010010";
        String third = "0";
        String fourth = "1";
        BitSet firstBitSet = idxToBitSet(first);
        BitSet secondBitSet = idxToBitSet(second);
        BitSet thirdBitSet = idxToBitSet(third);
        BitSet fourthBitSet = idxToBitSet(fourth);

        int firstIdx = grayConverterHelper.grayToIdx(firstBitSet);
        int secondIdx = grayConverterHelper.grayToIdx(secondBitSet);
        int thirdIdx = grayConverterHelper.grayToIdx(thirdBitSet);
        int fourthIdx = grayConverterHelper.grayToIdx(fourthBitSet);

        assertEquals(5, firstIdx);
        assertEquals(99, secondIdx);
        assertEquals(0, thirdIdx);
        assertEquals(1, fourthIdx);

    }

    @Test
    public void testIdxToGray() {
        int first = 5;
        int second = 99;
        int third = 0;
        int fourth = 1;

        BitSet firstBitSet = grayConverterHelper.idxToGray(first, getBitSetLength(first));
        BitSet secondBitSet = grayConverterHelper.idxToGray(second, getBitSetLength(second));
        BitSet thirdBitSet = grayConverterHelper.idxToGray(third, getBitSetLength(third));
        BitSet fourthBitSet = grayConverterHelper.idxToGray(fourth, getBitSetLength(fourth));

        String firstGrayAsString = bitSetToString(firstBitSet);
        String secondGrayAsString = bitSetToString(secondBitSet);
        String thirdGrayAsString = bitSetToString(thirdBitSet);
        String fourthGrayAsString = bitSetToString(fourthBitSet);

        assertEquals("111", firstGrayAsString);
        assertEquals("1010010", secondGrayAsString);
        assertEquals("0", thirdGrayAsString);
        assertEquals("1", fourthGrayAsString);
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
