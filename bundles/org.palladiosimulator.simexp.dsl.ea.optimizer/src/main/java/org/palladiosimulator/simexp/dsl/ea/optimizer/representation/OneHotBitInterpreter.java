package org.palladiosimulator.simexp.dsl.ea.optimizer.representation;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.OptimizableProcessingException;

public class OneHotBitInterpreter implements BitInterpreter {

    @Override
    public int toInt(SmodelBitset bitSet) {
        List<Integer> bitsSet = getBitsThatAreSet(bitSet);

        if (bitsSet.size() != 1) {
            throw new OptimizableProcessingException(
                    "Found an invalid One Hot encoding! Following bits where set: " + bitsSet.toString());
        }
        return bitsSet.get(0);
    }

    private List<Integer> getBitsThatAreSet(SmodelBitset bitSet) {
        List<Integer> bitsSet = new ArrayList();
        int nxtIdx = -1;
        do {
            nxtIdx = bitSet.nextSetBit(++nxtIdx);
            if (nxtIdx != -1)
                bitsSet.add(nxtIdx);
        } while (nxtIdx != -1);
        return bitsSet;
    }

    @Override
    public BitSet toBitSet(int value) {
        BitSet bitSet = new BitSet();
        bitSet.set(value);
        return bitSet;
    }

    @Override
    public boolean isValid(SmodelBitset bitSet, int numOfValues) {
        List<Integer> bitsThatAreSet = getBitsThatAreSet(bitSet);
        return (bitsThatAreSet.size() == 1) && (bitsThatAreSet.get(0) < numOfValues);
    }

    @Override
    public int getMinimumLength(int power) {
        return power;
    }

}
