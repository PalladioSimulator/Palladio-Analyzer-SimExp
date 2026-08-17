package org.palladiosimulator.simexp.dsl.ea.optimizer.representation;

import java.util.BitSet;

public class GrayBitInterpreter implements BitInterpreter {

    @Override
    public int toInt(SmodelBitset bitSet) {
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

    @Override
    public BitSet toBitSet(int value) {
        BitSet bitSet = new BitSet();
        int gray = value ^ (value >> 1);
        int i = 0;
        while (gray != 0) {
            if ((gray & 1) == 1) {
                bitSet.set(i);
            } else {
                bitSet.clear(i);
            }
            gray >>= 1;
            i++;
        }
        return bitSet;
    }

}
