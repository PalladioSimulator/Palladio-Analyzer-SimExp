package org.palladiosimulator.simexp.dsl.ea.optimizer.representation;

import static org.junit.Assert.assertEquals;

import java.util.BitSet;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.simexp.dsl.ea.optimizer.smodel.PowerUtil;

public class SmodelGrayBitsetTest {

    private SmodelGrayBitset grayBitset;

    @Before
    public void setUp() {
        grayBitset = new SmodelGrayBitset(10);
    }

    @Test
    public void testGrayToIdx0() {
        String bitString = "0";
        SmodelGrayBitset bitSet = idxToBitSet(bitString);

        int actualIdx = bitSet.toInt();

        assertEquals(0, actualIdx);
    }

    @Test
    public void testGrayToIdx1() {
        String bitString = "1";
        SmodelGrayBitset bitSet = idxToBitSet(bitString);

        int actualIdx = bitSet.toInt();

        assertEquals(1, actualIdx);
    }

    @Test
    public void testGrayToIdx5() {
        String bitString = "0111";
        SmodelGrayBitset bitSet = idxToBitSet(bitString);

        int actualIdx = bitSet.toInt();

        assertEquals(5, actualIdx);
    }

    @Test
    public void testGrayToIdx99() {
        String bitString = "1010010";
        SmodelGrayBitset bitSet = idxToBitSet(bitString);

        int actualIdx = bitSet.toInt();

        assertEquals(99, actualIdx);
    }

    @Test
    public void testIdxToGray0() {
        int value = 0;
        int bitSetLength = (new PowerUtil()).minBitSizeForPower(value);

        SmodelGrayBitset bitset = new SmodelGrayBitset(bitSetLength).fromInt(value);

        String grayString = bitSetToString(bitset);
        assertEquals("0", grayString);
    }

    @Test
    public void testIdxToGray1() {
        int value = 1;
        int bitSetLength = new PowerUtil().minBitSizeForPower(value);

        BitSet actualBitSet = new SmodelGrayBitset(bitSetLength).fromInt(value);

        String grayString = bitSetToString(actualBitSet);
        assertEquals("1", grayString);
    }

    @Test
    public void testIdxToGray5() {
        int value = 5;
        int bitSetLength = new PowerUtil().minBitSizeForPower(value);

        BitSet actualBitSet = new SmodelGrayBitset(bitSetLength).fromInt(value);

        String grayString = bitSetToString(actualBitSet);
        assertEquals("111", grayString);
    }

    @Test
    public void testIdxToGray99() {
        int value = 99;
        int bitSetLength = new PowerUtil().minBitSizeForPower(value);

        BitSet actualBitSet = new SmodelGrayBitset(bitSetLength).fromInt(value);

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

    private SmodelGrayBitset idxToBitSet(String first) {
        SmodelGrayBitset bitSet = new SmodelGrayBitset(first.length());
        int stringLength = first.length();
        for (int i = 0; i < stringLength; i++) {
            if (first.charAt(stringLength - 1 - i) == '1') {
                bitSet.set(i);
            }
        }
        return bitSet;
    }

}
