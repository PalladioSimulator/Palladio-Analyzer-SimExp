package org.palladiosimulator.simexp.dsl.ea.optimizer.impl.util;

import java.util.BitSet;

public class GrayConverterHelper {

    public static int grayToIdx(BitSet bitSet) {
        int idx = 0;
        for (int i = 0; i < bitSet.length(); i++) {
            if (bitSet.get(i)) {
                idx |= (1 << i);
            }
        }
        return idx;
    }

    public static BitSet idxToGray(int idx, int lengthBitSet) {
        int gray = idx ^ (idx >> 1);
        BitSet bitSet = new BitSet(lengthBitSet);
        int i = 0;

        while (gray != 0) {
            if ((gray & 1) == 1) {
                bitSet.set(i);
            }
            gray >>= 1;
            i++;
        }

        return bitSet;
    }

}
