package org.palladiosimulator.simexp.dsl.ea.optimizer.representation;

import java.util.BitSet;

public class BinaryBitInterpreter implements BitInterpreter {

    @Override
    public int toInt(SmodelBitset bitSet) {
        int value = 0;
        for (int i = 0; i < bitSet.length(); ++i) {
            value += bitSet.get(i) ? (1L << i) : 0L;
        }
        return value;

    }

    @Override
    public BitSet toBitSet(int value) {
        return BitSet.valueOf(new long[] { value });
    }

}
