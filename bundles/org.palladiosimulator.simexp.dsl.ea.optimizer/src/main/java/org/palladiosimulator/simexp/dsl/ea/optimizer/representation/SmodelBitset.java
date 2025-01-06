package org.palladiosimulator.simexp.dsl.ea.optimizer.representation;

import java.util.BitSet;

public class SmodelBitset extends FixedSizeBitSet {
    private static final long serialVersionUID = 1L;

    public SmodelBitset(int nbits) {
        super(nbits);
    }

    public int toInt() {
        int value = 0;
        for (int i = 0; i < length(); ++i) {
            value += get(i) ? (1L << i) : 0L;
        }
        return value;
    }

    public void fromInt(int value) {
        BitSet naiveBitSet = BitSet.valueOf(new long[] { value });
        if (naiveBitSet.length() > getNbits()) {
            throw new RuntimeException("Given value needs more bits to encode than this bitset has");
        }
        for (int i = 0; i < naiveBitSet.length(); i++) {
            if (naiveBitSet.get(i)) {
                set(i);
            }
        }
    }

}
