package org.palladiosimulator.simexp.dsl.ea.optimizer.representation;

import java.util.BitSet;

public class SmodelBitset extends FixedSizeBitSet {
    private static final long serialVersionUID = 1L;

    private BitInterpreter bitInterpreter;

    public SmodelBitset(int nbits, BitInterpreter bitInterpreter) {
        super(nbits);
        this.bitInterpreter = bitInterpreter;
    }

    public SmodelBitset(int nbits) {
        this(nbits, new BinaryBitInterpreter());
    }

    private SmodelBitset(int nbits, int value, BitInterpreter bitInterpreter) {
        this(nbits, bitInterpreter);
        BitSet naiveBitSet = bitInterpreter.toBitSet(value);
        if (naiveBitSet.length() > getNbits()) {
            throw new RuntimeException("Given value needs more bits to encode than this bitset has");
        }
        for (int i = 0; i < naiveBitSet.length(); i++) {
            if (naiveBitSet.get(i)) {
                set(i);
            }
        }
    }

    private SmodelBitset(int nbits, int value) {
        this(nbits, value, new BinaryBitInterpreter());
    }

    public int toInt() {
        return bitInterpreter.toInt(this);
    }

    public static SmodelBitset fromInt(int nbits, int value) {
        return new SmodelBitset(nbits, value);
    }

    public static SmodelBitset fromInt(int nbits, int value, BitInterpreter bitInterpreter) {
        return new SmodelBitset(nbits, value, bitInterpreter);
    }

}
