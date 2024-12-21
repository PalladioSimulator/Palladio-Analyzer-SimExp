package org.palladiosimulator.simexp.dsl.ea.optimizer.representation;

import java.util.BitSet;

public class IntToGrayConverter {

    public int grayToIdx(BitSet bitSet) {
        int idx = 0;
        if (bitSet.length() > 0) {
            if (bitSet.get(bitSet.length() - 1)) {
                idx |= (1 << (bitSet.length() - 1));
            }

            for (int i = bitSet.length() - 2; i >= 0; i--) {
                boolean previousBinary = ((1 << (i + 1)) & idx) != 0;
                if (bitSet.get(i) ^ previousBinary) {
                    idx |= (1 << i);
                }
            }
        }
        return idx;
    }

    public BitSet idxToGray(int idx, int lengthBitSet) {
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
